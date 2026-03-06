package com.fund.modules.stock.consumer

import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.Constants
import com.fund.common.RedisKeys
import com.fund.common.RedisKeys.STOCK_MESSAGE_QUEUE
import com.fund.common.RedisKeys.CHECK_ORDER_KEY
import com.fund.common.RedisKeys.CHECK_USER_POSITION_KEY
import com.fund.common.RedisKeys.USER_POSITION_CACHE_KEY
import com.fund.modules.emqt.co.MqttMsg
import com.fund.modules.emqt.service.EmqXService
import com.fund.modules.kline.event.KlineEvent
import com.fund.modules.quotation.service.UserQuotationControlService
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.model.UserPosition
import com.fund.modules.stock.model.UserPendingOrder
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.stock.service.UserPendingOrderService
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.vo.UserOrderVo
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.I18nUtil
import com.fund.utils.RedisLockService
import com.lmax.disruptor.RingBuffer
import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.redisson.api.listener.MessageListener
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

/**
 * 股票价格更新监听器 - 处理用户持仓的止盈止损检查
 */
@Component
class PositionUserUpdListener(
    private val redissonClient: RedissonClient,
    private val userPositionService: UserPositionService,
    private val userPendingOrderService: UserPendingOrderService,
    private val stockService: StockService,
    private val appUserWalletV2Service: AppUserWalletV2Service,
    private val klineRingBuffer: RingBuffer<KlineEvent>,
    private val i18nUtil: I18nUtil,
    private val emqXService: EmqXService,
    private val quotationControlService: UserQuotationControlService,
) : InitializingBean {

    private val logger = KotlinLogging.logger {}

    @Value("\${stock.position.listener.enabled:true}")
    private val listenerEnabled: Boolean = true

    @Value("\${stock.position.listener.max-retry:3}")
    private val maxRetry: Int = 3

    private val processedCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)

    override fun afterPropertiesSet() {
        logger.info("PositionUserUpdListener.afterPropertiesSet() start running")
        if (!listenerEnabled) {
            logger.info("股票持仓监听器已禁用")
            return
        }

        try {
            // 初始化 Redis 消息监听器
            val rTopic = redissonClient.getTopic(STOCK_MESSAGE_QUEUE)
            rTopic.addListener(String::class.java, MessageListener { channel, msg ->
                try {
                    processStockUpdateMessage(channel, msg)
                } catch (e: Exception) {
                    errorCount.incrementAndGet()
                    logger.error(e) { "处理股票更新消息时发生错误: channel=$channel, msg=$msg" }
                }
            })

            logger.info("股票持仓监听器初始化成功，监听频道: $STOCK_MESSAGE_QUEUE")
        } catch (e: Exception) {
            logger.error(e) { "初始化股票持仓监听器失败" }
            throw e
        }
    }

    /**
     * 发布 Stock 数据到 Disruptor
     */
    private fun publishToDisruptor(stock: Stock) {
        try {
            val sequence = klineRingBuffer.next()
            try {
                val event = klineRingBuffer[sequence]
                event.setData(stock)
            } finally {
                klineRingBuffer.publish(sequence)
            }
        } catch (e: Exception) {
            logger.error(e) { "发布 Stock 数据到 Disruptor 失败: ${stock.symbol}" }
        }
    }

    /**
     * 处理股票更新消息
     */
    fun processStockUpdateMessage(channel: CharSequence, message: String) {
        if (!listenerEnabled) {
            return
        }

        try {
            // 解析股票数据
            val stock = JSON.parseObject(message, Stock::class.java)
            if (stock?.id == null || stock.symbol.isNullOrEmpty()) {
                logger.warn("无效的股票数据: $message")
                return
            }

            emqXService.publish(MqttMsg(Constants.MARKET_THUMB, JSON.toJSONString(stock)))

            // 发布 Stock 数据到 Disruptor 进行 K线处理
            // publishToDisruptor(stock)

            // 检查是否有用户持仓该股票
            val cacheKey = String.format(CHECK_USER_POSITION_KEY, stock.flag + stock.symbol)
            val userSet = redissonClient.getSet<String>(cacheKey)

            if (!userSet.isExists) {
                return
            }

            // 批量处理用户持仓
            processUserPositions(stock, userSet)

            // 检查并处理挂单
            checkPendingOrders(stock)

            processedCount.incrementAndGet()

        } catch (e: Exception) {
            logger.error(e) { "解析股票更新消息失败: $message" }
        }
    }

    /**
     * 处理用户持仓的止盈止损检查
     */
    @Transactional(rollbackFor = [Exception::class])
    private fun processUserPositions(stock: Stock, userSet: Set<String>) {
        logger.info("接收到的数据是: ${JSON.toJSONString(stock)}, userSet size: ${userSet.size}")
        // 处理所有用户持仓，不限制数量
        for (userIdStr in userSet) {
            try {
                val userId = userIdStr.toLongOrNull() ?: continue
                processUserPosition(stock, userId)
            } catch (e: Exception) {
                logger.error(e) { "处理用户持仓失败: userId=$userIdStr, stockId=${stock.id}" }
            }
        }
    }

    /**
     * 处理单个用户的持仓
     */
    private fun processUserPosition(stock: Stock, userId: Long) {
        try {
            // 优先从缓存中获取用户持仓
            val stockFlag = stock.flag ?: ""
            val positions = getUserPositionsFromCache(userId, stockFlag, stock.symbol!!)

            // 获取用户调控配置，生成调整后的价格
            val adjustedStock = getAdjustedStockForUser(stock, userId)

            if (positions.isEmpty()) {
                // 缓存中没有，从数据库查询
                val dbPositions = userPositionService.list(
                    KtQueryWrapper(UserPosition())
                        .eq(UserPosition::userId, userId)
                        .eq(UserPosition::stockCode, stock.symbol)
                        .eq(UserPosition::status, "1") // 持仓中
                )

                if (dbPositions.isEmpty()) {
                    return
                }

                // 将数据库查询结果添加到缓存
                for (position in dbPositions) {
                    cacheUserPosition(position)
                    // 处理持仓（使用调整后的价格）
                    val key = RedisKeys.PROCESS_USER_POSITION_LOCK_KEY + position.id
                    RedisLockService.lockTransaction(key) {
                        updatePositionProfitLoss(position, adjustedStock)
                        checkProfitStopTarget(position, adjustedStock)
                    }
                }
            } else {
                // 从缓存中获取到持仓，直接处理（使用调整后的价格）
                for (position in positions) {
                    val key = RedisKeys.PROCESS_USER_POSITION_LOCK_KEY + position.id
                    RedisLockService.lockTransaction(key) {
                        updatePositionProfitLoss(position, adjustedStock)
                        checkProfitStopTarget(position, adjustedStock)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "处理用户持仓失败: userId=$userId, stockId=${stock.id}" }
        }
    }

    /**
     * 根据用户调控配置获取调整后的股票价格
     */
    private fun getAdjustedStockForUser(stock: Stock, userId: Long): Stock {
        val control = quotationControlService.getActiveControl(userId, stock.symbol!!, stock.flag!!)
        if (control?.floating == null || control.floating == BigDecimal.ZERO) {
            return stock
        }
        // 创建调整后的stock副本
        return Stock().apply {
            this.id = stock.id
            this.symbol = stock.symbol
            this.flag = stock.flag
            this.name = stock.name
            this.pId = stock.pId
            this.last = stock.last?.add(control.floating) // 调整价格
            this.high = stock.high
            this.low = stock.low
            this.chg = stock.chg
            this.chgPct = stock.chgPct
            this.volume = stock.volume
            this.time = stock.time
        }
    }

    /**
     * 从缓存中获取用户持仓
     */
    private fun getUserPositionsFromCache(userId: Long, stockFlag: String, stockSymbol: String): List<UserPosition> {
        try {
            // 1. 从股票持仓映射中获取持仓ID列表（使用 flag + symbol，与 updatePositionCache 保持一致）
            val stockPositionKey = "stock_positions:${stockFlag}${stockSymbol}"
            val stockPositionSet = redissonClient.getSet<String>(stockPositionKey)

            if (!stockPositionSet.isExists || stockPositionSet.isEmpty()) {
                return emptyList()
            }

            val positions = mutableListOf<UserPosition>()

            // 2. 从用户持仓缓存 Map 中根据持仓ID获取完整的持仓对象
            val positionCacheKey = String.format(USER_POSITION_CACHE_KEY, userId)
            val positionMap = redissonClient.getMap<String, String>(positionCacheKey)

            for (positionIdStr in stockPositionSet) {
                val positionJson = positionMap[positionIdStr]
                if (positionJson != null) {
                    val position = JSON.parseObject(positionJson, UserPosition::class.java)
                    if (position != null && position.userId == userId && position.stockCode == stockSymbol && position.status == "1") {
                        positions.add(position)
                    }
                }
            }

            return positions

        } catch (e: Exception) {
            logger.error(e) { "从缓存获取用户持仓失败: userId=$userId, stockSymbol=$stockSymbol" }
            return emptyList()
        }
    }

    /**
     * 缓存用户持仓对象
     */
    private fun cacheUserPosition(position: UserPosition) {
        try {
            val positionId = position.id?.toString() ?: return
            
            // 使用 Map 结构存储，支持一个用户多个持仓
            val positionCacheKey = String.format(USER_POSITION_CACHE_KEY, position.userId)
            val positionMap = redissonClient.getMap<String, String>(positionCacheKey)

            val positionJson = JSON.toJSONString(position)
            positionMap.put(positionId, positionJson)
            positionMap.expire(24, java.util.concurrent.TimeUnit.HOURS)

            // 同时更新股票持仓映射（使用 stockType + stockCode，与 updatePositionCache 保持一致）
            val stockType = position.stockType ?: ""
            val stockPositionKey = "stock_positions:${stockType}${position.stockCode}"
            val stockPositionSet = redissonClient.getSet<String>(stockPositionKey)
            stockPositionSet.add(positionId)

        } catch (e: Exception) {
            logger.error(e) { "缓存用户持仓失败: userId=${position.userId}, positionId=${position.id}" }
        }
    }

    /**
     * 更新持仓的实时盈亏数据
     */
    private fun updatePositionProfitLoss(position: UserPosition, stock: Stock) {
        try {
            val currentPrice = stock.last ?: return
            val buyPrice = position.buyOrderPrice ?: return

            // 计算实时盈亏
            val realTimeProfitLoss = calculateRealTimeProfitLoss(position, currentPrice)

            // 更新持仓对象的盈亏数据
            position.profitAndLose = realTimeProfitLoss
            position.allProfitAndLose = realTimeProfitLoss

            // 更新数据库
            userPositionService.updateById(position)

            // 更新缓存
            userPositionService.updatePositionCache(position, stock)

        } catch (e: Exception) {
            logger.error(e) { "更新持仓盈亏失败: userId=${position.userId}, positionId=${position.id}" }
        }
    }

    /**
     * 计算实时盈亏
     */
    private fun calculateRealTimeProfitLoss(position: UserPosition, currentPrice: BigDecimal): BigDecimal {
        val buyPrice = position.buyOrderPrice ?: BigDecimal.ZERO
        val orderNum = position.orderNum ?: BigDecimal.ZERO
        val lotUnit = position.lotUnit ?: 1
        val lever = position.orderLever ?: 1

        return when (position.orderDirection) {
            "买涨" -> {
                // 买涨：(当前价 - 买入价) * 数量 * Lot单位 * 杠杆
                currentPrice.subtract(buyPrice)
                    .multiply(orderNum)
                    .multiply(BigDecimal.valueOf(lotUnit.toLong()))
                    .multiply(BigDecimal.valueOf(lever.toLong()))
            }

            "买跌" -> {
                // 买跌：(买入价 - 当前价) * 数量 * Lot单位 * 杠杆
                buyPrice.subtract(currentPrice)
                    .multiply(orderNum)
                    .multiply(BigDecimal.valueOf(lotUnit.toLong()))
                    .multiply(BigDecimal.valueOf(lever.toLong()))
            }

            else -> BigDecimal.ZERO
        }.setScale(2, RoundingMode.HALF_UP)
    }

    /**
     * 检查止盈止损条件
     */
    private fun checkProfitStopTarget(position: UserPosition, stock: Stock) {
        val currentPrice = stock.last ?: return
        val buyType = position.orderDirection
        val profitTarget = position.profitTargetPrice
        val stopTarget = position.stopTargetPrice

        if (profitTarget == null && stopTarget == null) {
            return // 没有设置止盈止损
        }

        val shouldClosePosition = when (buyType) {
            "买涨" -> {
                // 买涨：价格达到止盈价或止损价时平仓
                (profitTarget != null && currentPrice.compareTo(profitTarget) >= 0) ||
                        (stopTarget != null && currentPrice.compareTo(stopTarget) <= 0)
            }

            "买跌" -> {
                // 买跌：价格达到止盈价或止损价时平仓
                (profitTarget != null && currentPrice.compareTo(profitTarget) <= 0) ||
                        (stopTarget != null && currentPrice.compareTo(stopTarget) >= 0)
            }

            else -> false
        }

        if (shouldClosePosition) {
            val closeReason = determineCloseReason(currentPrice, profitTarget, stopTarget, buyType)
            closePosition(position, stock, currentPrice, closeReason)
        }
    }

    /**
     * 确定平仓原因
     */
    private fun determineCloseReason(
        currentPrice: BigDecimal,
        profitTarget: BigDecimal?,
        stopTarget: BigDecimal?,
        buyType: String?
    ): String {
        return when (buyType) {
            "买涨" -> {
                when {
                    profitTarget != null && currentPrice.compareTo(profitTarget) >= 0 -> "止盈平仓"
                    stopTarget != null && currentPrice.compareTo(stopTarget) <= 0 -> "止损平仓"
                    else -> "价格触发平仓"
                }
            }

            "买跌" -> {
                when {
                    profitTarget != null && currentPrice.compareTo(profitTarget) <= 0 -> "止盈平仓"
                    stopTarget != null && currentPrice.compareTo(stopTarget) >= 0 -> "止损平仓"
                    else -> "价格触发平仓"
                }
            }

            else -> "价格触发平仓"
        }
    }

    /**
     * 执行平仓操作
     */
    private fun closePosition(position: UserPosition, stock: Stock, closePrice: BigDecimal, reason: String) {
        try {
            logger.info("开始自动平仓: 用户=${position.userId}, 股票=${stock.symbol}, 价格=$closePrice, 原因=$reason")

            // 1. 计算盈亏（调用公共方法）
            val profitLoss = userPositionService.calculateCloseProfitLoss(position, closePrice)
            logger.info("【自动平仓】盈亏计算: 买入价=${position.buyOrderPrice}, 平仓价=$closePrice, 盈亏=$profitLoss")

            // 2. 计算卖出总金额
            val buyNum = position.orderNum ?: BigDecimal.ZERO
            val allBuyAmt = position.orderTotalPrice ?: BigDecimal.ZERO
            val allSellAmt = closePrice.multiply(buyNum).multiply(BigDecimal(position.lotUnit ?: 1))
                .setScale(2, RoundingMode.HALF_UP)

            // 3. 计算总费用（调用公共方法）
            val allFeeAmt = userPositionService.calculateCloseFees(position, allSellAmt)
            logger.info("【自动平仓】费用计算: 卖出总额=$allSellAmt, 总费用=$allFeeAmt")

            // 4. 更新持仓状态
            position.status = "2" // 已平仓
            position.sellOrderTime = LocalDateTime.now()
            position.sellOrderPrice = closePrice
            position.profitAndLose = profitLoss
            position.allProfitAndLose = profitLoss.subtract(allFeeAmt)

            // 5. 保存更新
            userPositionService.updateById(position)

            // 6. 计算保证金
            val freezAmt = allBuyAmt.divide(BigDecimal(position.orderLever ?: 1), 2, RoundingMode.HALF_UP)

            // 7. 钱包结算（调用公共方法）
            val remark =
                "自动平仓($reason), id: ${position.id}(${position.stockCode}), 数量: $buyNum, 价格: $closePrice"
            userPositionService.settleCloseWallet(position, stock, position.allProfitAndLose!!, freezAmt, remark)

            // 8. 清理缓存（调用公共方法）
            userPositionService.clearCloseCache(position, stock)

            logger.info("平仓完成: 用户=${position.userId}, 股票=${stock.symbol}, 盈亏=${position.allProfitAndLose}, 原因=$reason")

        } catch (e: Exception) {
            logger.error(e) { "自动平仓操作失败: 用户=${position.userId}, 股票=${stock.symbol}" }
        }
    }

    /**
     * 检查并处理挂单
     */
    private fun checkPendingOrders(stock: Stock) {
        try {
            // 从Redis中获取挂单信息
            val key = String.format(CHECK_ORDER_KEY, stock.flag + stock.symbol)
            val pendingOrderMap = redissonClient.getMap<String, String>(key)

            if (!pendingOrderMap.isExists || pendingOrderMap.isEmpty()) {
                return
            }

            val currentPrice = stock.last ?: return

            // 遍历所有挂单
            for ((hKey, orderJson) in pendingOrderMap) {
                try {
                    // 只处理挂单类型（dataType=1），跳过止盈止损
                    if (!hKey.endsWith("1")) {
                        continue
                    }

                    val userOrderVo = JSON.parseObject(orderJson, UserOrderVo::class.java) ?: continue

                    // 确认是挂单类型
                    if (userOrderVo.dataType != "1") {
                        continue
                    }

                    val targetPrice = userOrderVo.amount ?: continue
                    val buyType = userOrderVo.buyType // "1"-买涨 "2"-买跌

                    // 检查价格是否达到目标价格
                    val shouldExecute = when (buyType) {
                        "1" -> currentPrice.compareTo(targetPrice) <= 0 // 买涨：当前价 <= 目标价
                        "2" -> currentPrice.compareTo(targetPrice) >= 0 // 买跌：当前价 >= 目标价
                        else -> false
                    }

                    if (shouldExecute) {
                        logger.info("挂单触发: 股票=${stock.symbol}, 用户=${userOrderVo.userId}, 目标价=$targetPrice, 当前价=$currentPrice, 类型=$buyType")

                        // 执行挂单买入
                        val lockKey = RedisKeys.PROCESS_USER_POSITION_LOCK_KEY + "pending_" + userOrderVo.id
                        RedisLockService.lockTransaction(lockKey) {
                            executePendingOrder(userOrderVo, stock, currentPrice)
                        }
                    }

                } catch (e: Exception) {
                    logger.error(e) { "处理单个挂单失败: hKey=$hKey" }
                }
            }

        } catch (e: Exception) {
            logger.error(e) { "检查挂单失败: 股票=${stock.symbol}" }
        }
    }

    /**
     * 执行挂单买入
     */
    private fun executePendingOrder(userOrderVo: UserOrderVo, stock: Stock, currentPrice: BigDecimal) {
        try {
            val userId = userOrderVo.userId?.toLongOrNull() ?: return
            val pendingOrderId = userOrderVo.id?.toLongOrNull() ?: return

            logger.info("开始执行挂单买入: 用户=$userId, 挂单ID=$pendingOrderId, 股票=${stock.symbol}")

            // 调用新的买入方法
            val result = userPositionService.buyFromPendingOrder(pendingOrderId, userId, currentPrice)

            if (result.code == 0) {
                // 买入成功，更新挂单状态和清理Redis缓存
                userPendingOrderService.updatePendingOrderStatus(pendingOrderId, 1, null)

                // 从Redis中删除挂单信息
                val key = String.format(CHECK_ORDER_KEY, stock.flag + stock.symbol)
                val hKey = "${pendingOrderId}1"
                val pendingOrderMap = redissonClient.getMap<String, String>(key)
                pendingOrderMap.remove(hKey)

                logger.info("挂单执行成功: 用户=$userId, 挂单ID=$pendingOrderId, 股票=${stock.symbol}")
            } else {
                // 买入失败，更新挂单状态为失败
                userPendingOrderService.updatePendingOrderStatus(pendingOrderId, 2, result.msg)

                // 从Redis中删除挂单信息
                val key = String.format(CHECK_ORDER_KEY, stock.flag + stock.symbol)
                val hKey = "${pendingOrderId}1"
                val pendingOrderMap = redissonClient.getMap<String, String>(key)
                pendingOrderMap.remove(hKey)

                logger.warn("挂单执行失败: 用户=$userId, 挂单ID=$pendingOrderId, 原因=${result.msg}")
            }

        } catch (e: Exception) {
            logger.error(e) { "执行挂单买入失败: 用户=${userOrderVo.userId}, 挂单ID=${userOrderVo.id}" }
            // 更新挂单状态为失败
            try {
                val pendingOrderId = userOrderVo.id?.toLongOrNull()
                if (pendingOrderId != null) {
                    userPendingOrderService.updatePendingOrderStatus(pendingOrderId, 2, e.message)
                }
            } catch (ex: Exception) {
                logger.error(ex) { "更新挂单失败状态时出错" }
            }
        }
    }

    /**
     * 获取监听器统计信息
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "processedCount" to processedCount.get(),
            "errorCount" to errorCount.get(),
            "listenerEnabled" to listenerEnabled,
            "maxRetry" to maxRetry
        )
    }
}