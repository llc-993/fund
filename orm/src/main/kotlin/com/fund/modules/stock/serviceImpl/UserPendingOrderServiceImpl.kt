package com.fund.modules.stock.serviceImpl;


import cn.hutool.core.util.ObjectUtil
import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys.CHECK_ORDER_KEY
import com.fund.common.RedisKeys.PENDING_ORDER_KEY
import com.fund.common.RedisKeys.STOCK_KEY
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.stock.StockAddOrderRequest
import com.fund.modules.stock.mapper.UserPendingOrderMapper
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.model.UserPendingOrder
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.vo.UserOrderVo
import com.fund.modules.stock.service.UserPendingOrderService
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.user.service.AppUserService
import com.fund.utils.I18nUtil
import com.fund.utils.GeneratorIdUtil
import com.fund.utils.RedisLockService
import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service;
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 用户挂单表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
@Service
open class UserPendingOrderServiceImpl(
    private val appUserService: AppUserService,
    @Lazy private val userPositionService: UserPositionService,
    private val i18nUtil: I18nUtil,
    private val redissonClient: RedissonClient,
    private val stockService: StockService
) : ServiceImpl<UserPendingOrderMapper, UserPendingOrder>(), UserPendingOrderService {

    private val logger = KotlinLogging.logger {}

    /**
     * 添加股票挂单
     * @param req 股票下单请求参数
     * @param userId 用户ID
     * @return 操作结果
     */
    override fun addOrder(req: StockAddOrderRequest, userId: Long): R<Any> {
        // 1. 获取用户信息并验证交易权限
        val appUser = appUserService.getById(userId)
        if (appUser.tradable!!) { // 用户账户被锁定，不允许交易
            return R.error(i18nUtil.getMessage("account_is_locked"))
        }

        // 2. 验证购买数量
        // 检查买入数量是否为空，如果为空则抛出异常
        req.buyNum?.let { buyNum ->
            val buyQuantity = BigDecimal.valueOf(buyNum.toLong())
            // 调用持仓服务验证购买数量是否符合规则（如最小/最大购买限制）
            userPositionService.validateBuyQuantity(buyQuantity)
            logger.info("用户 $userId 购买数量 $buyNum 验证通过")
        } ?: throw BusinessException("buy_not_empty")

        // 3. 验证止盈止损价格设置
        // 只有当目标价格存在时才进行价格验证
        req.targetPrice?.let { targetPrice ->
            req.buyType?.let { buyType ->
                if (buyType == 0) { // 买入订单
                    // 3.1 验证买入止盈价格
                    // 买入时：止盈价格不能低于目标报价（否则无法盈利）
                    req.profitTarget?.let { profitTarget ->
                        if (profitTarget.compareTo(targetPrice) < 0) {
                            return R.error(i18nUtil.getMessage("buy_profit_target_too_low") + i18nUtil.getMessage("current_quote") + targetPrice)
                        }
                    }

                    // 3.2 验证买入止损价格
                    // 买入时：止损价格不能高于目标报价（否则止损无意义）
                    req.stopTarget?.let { stopTarget ->
                        if (stopTarget.compareTo(targetPrice) > 0) {
                            return R.error(i18nUtil.getMessage("buy_stop_loss_too_high") + i18nUtil.getMessage("current_quote") + targetPrice)
                        }
                    }
                } else { // 卖出订单
                    // 3.3 验证卖出止盈价格
                    // 卖出时：止盈价格不能高于目标报价（否则无法盈利）
                    req.profitTarget?.let { profitTarget ->
                        if (profitTarget.compareTo(targetPrice) > 0) {
                            return R.error(i18nUtil.getMessage("sell_profit_target_too_high") + i18nUtil.getMessage("current_quote") + targetPrice)
                        }
                    }

                    // 3.4 验证卖出止损价格
                    // 卖出时：止损价格不能低于目标报价（否则止损无意义）
                    req.stopTarget?.let { stopTarget ->
                        if (stopTarget.compareTo(targetPrice) < 0) {
                            return R.error(i18nUtil.getMessage("sell_stop_loss_too_low") + i18nUtil.getMessage("current_quote") + targetPrice)
                        }
                    }
                }
            }
        }

        val stock = stockService.getStockById(req.stockId!!.toLong())
        if (ObjectUtil.isEmpty(stock)) {
            logger.info("股票代码不存在: ${req.stockId}")
            throw BusinessException("stock_not_found")
        }

        val count = this.count(
            KtQueryWrapper(UserPendingOrder::class.java)
                .eq(UserPendingOrder::stockId, req.stockId!!.toLong())
                .eq(UserPendingOrder::userId, userId)
                .eq(UserPendingOrder::status, 0)
        )
        if (count > 0) {
            return R.error(i18nUtil.getMessage("not_pending_orders"))
        }

        // 5. 获取股票当前价格
        val key1 = STOCK_KEY + stock.flag + stock.symbol
        val bucket = redissonClient.getBucket<String>(key1)
        val stockData = JSON.parseObject(bucket.get(), Stock::class.java)

        if (stockData?.last == null) {
            logger.error("无法获取股票当前价格: ${stock.symbol}")
            throw BusinessException("current_quote")
        }
        val key2 = PENDING_ORDER_KEY + userId
        RedisLockService.lockTransaction(key2) {

            // 6. 创建挂单对象并设置属性
            val userPendingOrder = UserPendingOrder().apply {
                this.userId = userId.toInt()
                this.stockId = req.stockId
                this.buyNum = req.buyNum
                this.buyType = req.buyType
                this.lever = req.lever
                this.profitTarget = req.profitTarget
                this.stopTarget = req.stopTarget
                this.nowPrice = stockData.last
                this.targetPrice = req.targetPrice
                this.addTime = LocalDateTime.now()
                this.status = 0 // 0: 已挂单
                this.stockType = req.stockType
                this.orderNo = GeneratorIdUtil.generateId()
            }

            // 7. 保存挂单到数据库
            val ret = this.save(userPendingOrder)
            if (!ret) {
                logger.error("保存挂单失败: 用户=$userId, 股票=${req.stockId}")
                throw BusinessException("operation_failed")
            }
             logger.info("挂单创建成功: 用户=$userId, 股票=${req.stockId}, 订单号=${userPendingOrder.orderNo}")
             
             // 8. 创建UserOrderVo对象并缓存到Redis
             val vo = UserOrderVo().apply {
                 this.id = userPendingOrder.id?.toString()
                 this.userId = userId.toString()
                 this.stockCode = stock.symbol
                 this.stockType = req.stockType
                 this.buyType = if (req.buyType == 0) "1" else "2" // 0-买涨转为1, 其他转为2-买跌
                 this.dataType = "1" // 1-挂单购入
                 this.amount = req.targetPrice
                 this.oldNowPrice = stockData.last
             }
             
             // 9. 缓存到Redis
             val key = String.format(CHECK_ORDER_KEY, stock.flag + stock.symbol)
             val hKey = "${userPendingOrder.id}1" // 挂单ID + "1"作为hash key
             val redisMap = redissonClient.getMap<String, String>(key)
             redisMap.put(hKey, JSON.toJSONString(vo))
             
             logger.info("挂单信息已缓存到Redis: key=$key, hKey=$hKey")
         }

         // 10. 所有操作完成，返回成功
         return R.success("挂单创建成功")
    }

    override fun delOrder(id: Long, userId: Long): R<Any> {
        return RedisLockService.lockTransaction(PENDING_ORDER_KEY + userId) block@ {
            this.remove(KtQueryWrapper(UserPendingOrder::class.java)
                .eq(UserPendingOrder::userId, userId)
                .eq(UserPendingOrder::id, id)
            )
            R.success()
        }
    }

    /**
     * 更新挂单状态
     */
    override fun updatePendingOrderStatus(pendingOrderId: Long, status: Int, failReason: String?) {
        try {
            val pendingOrder = this.getById(pendingOrderId)
            if (pendingOrder != null) {
                pendingOrder.status = status.toByte()
                pendingOrder.failReason = failReason
                
                val updateResult = this.updateById(pendingOrder)
                if (updateResult) {
                    logger.info("更新挂单状态成功: pendingOrderId=$pendingOrderId, status=$status, failReason=$failReason")
                } else {
                    logger.error("更新挂单状态失败: pendingOrderId=$pendingOrderId")
                }
            } else {
                logger.warn("挂单不存在: pendingOrderId=$pendingOrderId")
            }
        } catch (e: Exception) {
            logger.error(e) { "更新挂单状态时发生错误: pendingOrderId=$pendingOrderId, status=$status" }
        }
    }

}
