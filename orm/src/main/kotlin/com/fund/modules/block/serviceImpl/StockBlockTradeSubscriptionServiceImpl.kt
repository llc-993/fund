package com.fund.modules.block.serviceImpl;

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.modules.block.model.StockBlockTradeSubscription;
import com.fund.modules.block.mapper.StockBlockTradeSubscriptionMapper;
import com.fund.modules.block.service.StockBlockTradeSubscriptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.block.BlockTradeApplyRequest
import com.fund.modules.block.BlockTradeUpdateRequest
import com.fund.modules.block.service.StockBlockTradeService
import com.fund.modules.stock.service.StockService
import com.fund.modules.user.service.AppUserService
import com.fund.utils.GeneratorIdUtil
import com.fund.utils.I18nUtil
import com.fund.utils.RedisLockService
import mu.KotlinLogging
import org.springframework.stereotype.Service;
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 大宗交易申购记录 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-16
 */
@Service
open class StockBlockTradeSubscriptionServiceImpl(
    private val stockBlockTradeService: StockBlockTradeService,
    private val stockService: StockService,
    private val appUserService: AppUserService,
    private val i18nUtil: I18nUtil,
) : ServiceImpl<StockBlockTradeSubscriptionMapper, StockBlockTradeSubscription>(), StockBlockTradeSubscriptionService {

    private val logger = KotlinLogging.logger {}

    /**
     * 大宗交易申购
     *
     * 业务逻辑：
     * 1. 验证用户和大宗交易信息
     * 2. 检查大宗交易状态和时间范围
     * 3. 验证申购数量是否在允许范围内
     * 4. 获取股票当前价格并应用折扣计算实际价格
     * 5. 创建申购记录（状态=1 已申购）
     *
     * 大宗交易与IPO的区别：
     * - IPO: 通过symbol查询股票，价格固定
     * - 大宗交易: 通过stockId查询股票，价格=当前价格×折扣
     * - IPO: 锁仓状态 isLock (0=未锁，1=锁)
     * - 大宗交易: 锁定状态 lockStatus (1=锁定，2=不锁定)
     * - 大宗交易支持分阶段释放锁定（通过 firstReleaseLookRate 和 releaseLookTime）
     */
    override fun apply(req: BlockTradeApplyRequest, userId: Long): R<Any> {

        val key = RedisKeys.BLOCK_TRADE_APPLY_LOCK_KEY + userId
        return RedisLockService.lockTransaction(key) block@{

            val appUser = appUserService.getById(userId) ?: throw BusinessException("user_null")
            if (appUser.tradable!!) { // 不允许交易
                throw BusinessException("account_is_locked")
            }
            val blockTrade =
                stockBlockTradeService.getById(req.blockTradeId) ?: throw BusinessException("block_trade_null")

            // 检查状态（1=开放中）
            if (blockTrade.status != 1) {
                throw BusinessException("block_trade_not_available")
            }

            // 获取当前时间
            val currentTime = LocalDateTime.now()

            // 如果设置了开始时间，检查大宗交易是否已经开始
            blockTrade.startDateTime?.let { startDateTime ->
                if (currentTime.isBefore(startDateTime)) {
                    throw BusinessException("block_trade_not_started")
                }
            }

            // 如果设置了结束时间，检查大宗交易是否已经结束
            blockTrade.endDateTime?.let { endDateTime ->
                if (currentTime.isAfter(endDateTime)) {
                    throw BusinessException("block_trade_has_ended")
                }
            }

            // 验证申购数量
            val applyNums = req.applyNums ?: throw BusinessException("missing_key_parameter")

            // 检查最小申购数量
            blockTrade.minAmount?.let { minAmount ->
                if (applyNums.toBigDecimal() < minAmount) {
                    val errorMsg = i18nUtil.getMessage("block_trade_min_num_limit", minAmount.toString(), minAmount)
                    throw BusinessException(errorMsg)
                }
            }

            // 检查最大申购数量
            blockTrade.maxAmount?.let { maxAmount ->
                if (applyNums.toBigDecimal() > maxAmount) {
                    val errorMsg = i18nUtil.getMessage("block_trade_max_num_limit", maxAmount.toString(), maxAmount)
                    throw BusinessException(errorMsg)
                }
            }

            // 获取股票信息
            val stock = stockService.getById(blockTrade.stockId?.toLong()) ?: throw BusinessException("stock_null")

            // 计算购买价格（应用折扣）
            val originalPrice = stock.last ?: BigDecimal.ZERO
            val discount = blockTrade.discount ?: BigDecimal.ONE
            val buyPrice = originalPrice.multiply(discount)
            val actualAmount = buyPrice.multiply(applyNums.toBigDecimal())

            val subscription = StockBlockTradeSubscription()
            subscription.orderNo = GeneratorIdUtil.generateId()
            subscription.userId = userId
            subscription.topUserId = appUser.topUserId
            subscription.blockTradeId = req.blockTradeId
            subscription.applyNums = applyNums.toBigDecimal()
            subscription.buyPrice = buyPrice
            subscription.discount = discount
            subscription.actualAmount = actualAmount
            subscription.status = 1
            subscription.createTime = LocalDateTime.now()
            subscription.name = stock.name
            subscription.stockId = stock.id
            subscription.submitTime = LocalDateTime.now()

            this.save(subscription)
            R.success()
        }
    }

    override fun history(userId: Long): R<Any> {
        val list = this.list(
            KtQueryWrapper(StockBlockTradeSubscription())
                .eq(StockBlockTradeSubscription::userId, userId)
                .orderByDesc(StockBlockTradeSubscription::id)
        )
        for (subscription in list) {
            subscription.stockBlockTrade = stockBlockTradeService.getById(subscription.blockTradeId)
        }
        return R.success(list)
    }

    override fun update(req: BlockTradeUpdateRequest, userId: Long): R<Any> {
        try {
            val subscriptionId = req.id ?: throw BusinessException("missing_key_parameter")

            val key = RedisKeys.BLOCK_TRADE_APPLY_LOCK_KEY + subscriptionId
            return RedisLockService.lockTransaction(key) block@{

                // 查询申购记录
                val subscription = this.getOne(
                    KtQueryWrapper(StockBlockTradeSubscription())
                        .eq(StockBlockTradeSubscription::userId, userId)
                        .eq(StockBlockTradeSubscription::id, subscriptionId)
                ) ?: throw BusinessException("block_trade_subscription_not_found")

                // 检查状态，只有状态为1（已申购）时才能修改
                if (subscription.status != 1) {
                    throw BusinessException("block_trade_subscription_cannot_modify")
                }

                // 获取大宗交易信息
                val blockTrade = stockBlockTradeService.getById(subscription.blockTradeId)
                    ?: throw BusinessException("block_trade_null")

                // 验证申购数量
                val applyNums = req.applyNums ?: throw BusinessException("missing_key_parameter")

                // 检查最小申购数量
                blockTrade.minAmount?.let { minAmount ->
                    if (applyNums.toBigDecimal() < minAmount) {
                        val errorMsg = i18nUtil.getMessage("block_trade_min_num_limit", minAmount.toString(), minAmount)
                        throw BusinessException(errorMsg)
                    }
                }

                // 检查最大申购数量
                blockTrade.maxAmount?.let { maxAmount ->
                    if (applyNums.toBigDecimal() > maxAmount) {
                        val errorMsg = i18nUtil.getMessage("block_trade_max_num_limit", maxAmount.toString(), maxAmount)
                        throw BusinessException(errorMsg)
                    }
                }

                // 更新申购数量和实际金额
                subscription.applyNums = applyNums.toBigDecimal()
                subscription.actualAmount = subscription.buyPrice?.multiply(applyNums.toBigDecimal())
                this.updateById(subscription)

                R.success()
            }
        } catch (e: Exception) {
            logger.error(e) { "大宗交易修改申购异常 " }
            throw BusinessException("fail")
        }
    }

}

