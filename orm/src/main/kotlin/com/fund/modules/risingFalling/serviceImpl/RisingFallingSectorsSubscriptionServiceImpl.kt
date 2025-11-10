package com.fund.modules.risingFalling.serviceImpl;

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.modules.risingFalling.model.RisingFallingSectorsSubscription;
import com.fund.modules.risingFalling.mapper.RisingFallingSectorsSubscriptionMapper;
import com.fund.modules.risingFalling.service.RisingFallingSectorsSubscriptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.conf.enum.AppConfigCode
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.risingFalling.RisingFallingSectorsApplyRequest
import com.fund.modules.risingFalling.RisingFallingSectorsUpdateRequest
import com.fund.modules.risingFalling.service.RisingFallingSectorsService
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
 * 涨跌板块申购记录 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-17
 */
@Service
open class RisingFallingSectorsSubscriptionServiceImpl(
    private val risingFallingSectorsService: RisingFallingSectorsService,
    private val stockService: StockService,
    private val appUserService: AppUserService,
    private val i18nUtil: I18nUtil,
    private val appConfigService: AppConfigService
) : ServiceImpl<RisingFallingSectorsSubscriptionMapper, RisingFallingSectorsSubscription>(), RisingFallingSectorsSubscriptionService {

    private val logger = KotlinLogging.logger {}

    /**
     * 涨跌板块申购
     * 
     * 业务逻辑：
     * 1. 验证用户和涨跌板块信息
     * 2. 检查涨跌板块状态和时间范围
     * 3. 校验交易密码（如果设置了密码）
     * 4. 验证申购数量是否在允许范围内
     * 5. 获取股票当前价格作为购买价格
     * 6. 创建申购记录（状态=1 已申购）
     * 
     * 涨跌板块与IPO、大宗交易的区别：
     * - IPO: 通过symbol查询股票，固定价格，可能未中签
     * - 大宗交易: 通过stockId查询股票，价格=当前价格×折扣
     * - 涨跌板块: 通过stockId查询股票，价格=当前市场价格
     * - 涨跌板块: 基于板块概念，支持密码保护（passWord字段）
     * - 涨跌板块: 支持显示状态控制（0=显示，1=隐藏）
     * - 涨跌板块: 支持交易密码校验，只有输入正确密码才能申购
     */
    override fun apply(req: RisingFallingSectorsApplyRequest, userId: Long): R<Any> {

            val key = RedisKeys.RISING_FALLING_SECTORS_APPLY_LOCK_KEY + userId
            return RedisLockService.lockTransaction(key) block@{

                val appUser = appUserService.getById(userId) ?: throw BusinessException("user_null")
                if (appUser.tradable!!) { // 不允许交易
                    throw BusinessException("account_is_locked")
                }
                val risingFallingSectors = risingFallingSectorsService.getById(req.risingFallingSectorsId) ?: throw BusinessException("rising_falling_sectors_null")

                // 检查显示状态（0=显示）
                if (risingFallingSectors.displayStatus != 0) {
                    throw BusinessException("rising_falling_sectors_not_available")
                }

                // 校验交易密码（如果涨跌板块设置了密码）
                risingFallingSectors.passWord?.let { passWord ->
                    if (passWord.isNotBlank()) {
                        val inputPassword = req.tradePassword ?: throw BusinessException("trade_password_required")
                        if (inputPassword != passWord) {
                            throw BusinessException("trade_password_incorrect")
                        }
                    }
                }

                // 获取当前时间
                val currentTime = LocalDateTime.now()

                // 如果设置了开始售卖时间，检查涨跌板块是否已经开始售卖
                risingFallingSectors.startSellTime?.let { startSellTime ->
                    if (currentTime.isBefore(startSellTime)) {
                        throw BusinessException("rising_falling_sectors_not_started")
                    }
                }

                // 如果设置了结束售卖时间，检查涨跌板块是否已经结束售卖
                risingFallingSectors.endSellTime?.let { endSellTime ->
                    if (currentTime.isAfter(endSellTime)) {
                        throw BusinessException("rising_falling_sectors_has_ended")
                    }
                }

                // 验证申购数量
                val applyNums = req.applyNums ?: throw BusinessException("missing_key_parameter")

                // 获取最小申购数量配置
                val minNum = appConfigService.getValueOrDefault(AppConfigCode.RISING_FALLING_MIN_NUM)?.toIntOrNull() ?: 1
                if (applyNums < minNum) {
                    val errorMsg = i18nUtil.getMessage("rising_falling_min_num_limit", minNum.toString(), minNum)
                    throw BusinessException(errorMsg)
                }

                // 获取最大申购数量配置
                val maxNum = appConfigService.getValueOrDefault(AppConfigCode.RISING_FALLING_MAX_NUM)?.toIntOrNull() ?: 10000
                if (applyNums > maxNum) {
                    val errorMsg = i18nUtil.getMessage("rising_falling_max_num_limit", maxNum.toString(), maxNum)
                    throw BusinessException(errorMsg)
                }

                // 获取股票信息
                val stock = stockService.getById(risingFallingSectors.stockId) ?: throw BusinessException("stock_null")

                // 使用股票当前价格作为购买价格
                val buyPrice = stock.last ?: BigDecimal.ZERO
                if (buyPrice.compareTo(BigDecimal.ZERO) == 0) {
                    throw BusinessException("stock_price_zero")
                }
                val actualAmount = buyPrice.multiply(applyNums.toBigDecimal())

                val subscription = RisingFallingSectorsSubscription()
                subscription.orderNo = GeneratorIdUtil.generateId()
                subscription.userId = userId
                subscription.topUserId = appUser.topUserId
                subscription.risingFallingSectorsId = req.risingFallingSectorsId
                subscription.applyNums = applyNums.toBigDecimal()
                subscription.buyPrice = buyPrice
                subscription.actualAmount = actualAmount
                subscription.status = 1
                subscription.createTime = LocalDateTime.now()
                subscription.name = stock.name
                subscription.stockId = stock.id
                subscription.symbol = stock.symbol
                subscription.submitTime = LocalDateTime.now()

                this.save(subscription)
                R.success()
            }

    }

    override fun history(userId: Long): R<Any> {
        val list = this.list(
            KtQueryWrapper(RisingFallingSectorsSubscription())
                .eq(RisingFallingSectorsSubscription::userId, userId)
                .orderByDesc(RisingFallingSectorsSubscription::id)
        )
        return R.success(list)
    }

    override fun update(req: RisingFallingSectorsUpdateRequest, userId: Long): R<Any> {
            val subscriptionId = req.id ?: throw BusinessException("missing_key_parameter")
            
            val key = RedisKeys.RISING_FALLING_SECTORS_APPLY_LOCK_KEY + subscriptionId
            return RedisLockService.lockTransaction(key) block@{
                
                // 查询申购记录
                val subscription = this.getOne(KtQueryWrapper(RisingFallingSectorsSubscription())
                    .eq(RisingFallingSectorsSubscription::userId, userId)
                    .eq(RisingFallingSectorsSubscription::id, subscriptionId)
                )?: throw BusinessException("rising_falling_sectors_subscription_not_found")
                
                // 检查状态，只有状态为1（已申购）时才能修改
                if (subscription.status != 1) {
                    throw BusinessException("rising_falling_sectors_subscription_cannot_modify")
                }
                
                // 验证申购数量
                val applyNums = req.applyNums ?: throw BusinessException("missing_key_parameter")
                
                // 获取最小申购数量配置
                val minNum = appConfigService.getValueOrDefault(AppConfigCode.RISING_FALLING_MIN_NUM)?.toIntOrNull() ?: 1
                if (applyNums < minNum) {
                    val errorMsg = i18nUtil.getMessage("rising_falling_min_num_limit", minNum.toString(), minNum)
                    throw BusinessException(errorMsg)
                }
                
                // 获取最大申购数量配置
                val maxNum = appConfigService.getValueOrDefault(AppConfigCode.RISING_FALLING_MAX_NUM)?.toIntOrNull() ?: 10000
                if (applyNums > maxNum) {
                    val errorMsg = i18nUtil.getMessage("rising_falling_max_num_limit", maxNum.toString(), maxNum)
                    throw BusinessException(errorMsg)
                }
                
                // 更新申购数量和实际金额
                subscription.applyNums = applyNums.toBigDecimal()
                subscription.actualAmount = subscription.buyPrice?.multiply(applyNums.toBigDecimal())
                this.updateById(subscription)
                
                R.success()
            }
    }

}
