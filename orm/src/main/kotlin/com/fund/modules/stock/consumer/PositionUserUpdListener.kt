package com.fund.modules.stock.consumer

import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.RedisKeys
import com.fund.common.RedisKeys.STOCK_MESSAGE_QUEUE
import com.fund.common.RedisKeys.CHECK_ORDER_KEY
import com.fund.common.RedisKeys.CHECK_USER_POSITION_KEY
import com.fund.common.RedisKeys.USER_POSITION_CACHE_KEY
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.model.UserPosition
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.stock.service.StockService
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.I18nUtil
import com.fund.utils.RedisLockService
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
    private val stockService: StockService,
    private val appUserWalletV2Service: AppUserWalletV2Service,
    private val i18nUtil: I18nUtil
) : InitializingBean {

    private val logger = KotlinLogging.logger {}

    @Value("\${stock.position.listener.enabled:true}")
    private val listenerEnabled: Boolean = true


    @Value("\${stock.position.listener.max-retry:3}")
    private val maxRetry: Int = 3

    private val processedCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)

    override fun afterPropertiesSet() {
        if (!listenerEnabled) {
            logger.info("股票持仓监听器已禁用")
            return
        }

        try {
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
     * 处理股票更新消息
     */
    private fun processStockUpdateMessage(channel: CharSequence, message: String) {
        if (!listenerEnabled) {
            return
        }

        try {
            // 解析股票数据
            val stock = JSON.parseObject(message, Stock::class.java)
            if (stock?.pId == null || stock.symbol.isNullOrEmpty()) {
                logger.warn("无效的股票数据: $message")
                return
            }

            logger.debug("收到股票更新消息: 股票ID=${stock.id}, 符号=${stock.symbol}, 价格=${stock.last}")

            // 检查是否有用户持仓该股票
            val cacheKey = String.format(CHECK_USER_POSITION_KEY, stock.flag + stock.symbol)
            val userSet = redissonClient.getSet<String>(cacheKey)

            if (!userSet.isExists) {
                logger.debug("没有用户持仓股票: ${stock.symbol}")
                return
            }

            // 批量处理用户持仓
            processUserPositions(stock, userSet)

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
            val positions = getUserPositionsFromCache(userId, stock.symbol!!)

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
                    // 处理持仓
                    val key = RedisKeys.PROCESS_USER_POSITION_LOCK_KEY + position.id
                    RedisLockService.lockTransaction(key) {
                        updatePositionProfitLoss(position, stock)
                        checkProfitStopTarget(position, stock)
                    }
                }
            } else {
                // 从缓存中获取到持仓，直接处理
                for (position in positions) {
                    val key = RedisKeys.PROCESS_USER_POSITION_LOCK_KEY + position.id
                    RedisLockService.lockTransaction(key) {
                        updatePositionProfitLoss(position, stock)
                        checkProfitStopTarget(position, stock)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "处理用户持仓失败: userId=$userId, stockId=${stock.id}" }
        }
    }

    /**
     * 从缓存中获取用户持仓
     */
    private fun getUserPositionsFromCache(userId: Long, stockSymbol: String): List<UserPosition> {
        try {
            // 1. 从股票持仓映射中获取持仓ID列表
            val stockPositionKey = "stock_positions:${stockSymbol}"
            val stockPositionSet = redissonClient.getSet<String>(stockPositionKey)

            if (!stockPositionSet.isExists || stockPositionSet.isEmpty()) {
                return emptyList()
            }

            val positions = mutableListOf<UserPosition>()

            // 2. 根据持仓ID从缓存中获取完整的持仓对象
            for (positionIdStr in stockPositionSet) {
                val positionId = positionIdStr.toLongOrNull() ?: continue

                // 从用户持仓缓存中获取
                val positionCacheKey = String.format(USER_POSITION_CACHE_KEY, userId)
                val positionBucket = redissonClient.getBucket<String>(positionCacheKey)

                if (positionBucket.isExists) {
                    val positionJson = positionBucket.get()
                    if (positionJson != null) {
                        val position = JSON.parseObject(positionJson, UserPosition::class.java)
                        if (position != null && position.stockCode == stockSymbol && position.status == "1") {
                            positions.add(position)
                        }
                    }
                }
            }

            logger.debug("从缓存获取用户持仓: userId=$userId, stockSymbol=$stockSymbol, 数量=${positions.size}")
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
            val positionCacheKey = String.format(USER_POSITION_CACHE_KEY, position.userId)
            val positionBucket = redissonClient.getBucket<String>(positionCacheKey)

            val positionJson = JSON.toJSONString(position)
            positionBucket.set(positionJson, 24, java.util.concurrent.TimeUnit.HOURS)

            // 同时更新股票持仓映射
            val stockPositionKey = "stock_positions:${position.stockCode}"
            val stockPositionSet = redissonClient.getSet<String>(stockPositionKey)
            stockPositionSet.add(position.id?.toString() ?: "")

            logger.debug("缓存用户持仓: userId=${position.userId}, positionId=${position.id}, stockCode=${position.stockCode}")

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
            updatePositionCache(position, stock)

            logger.debug("更新持仓盈亏: userId=${position.userId}, positionId=${position.id}, 实时盈亏=$realTimeProfitLoss")

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
     * 钱包结算
     */
    private fun settleWalletBalance(position: UserPosition, stock: Stock, profitLoss: BigDecimal, reason: String) {
        try {
            val userId = position.userId?.toLong() ?: return
            val coin = appUserWalletV2Service.getCoinByStockFlag(stock.flag)

            // 查找用户钱包
            val wallet = appUserWalletV2Service.findWalletByUserAndType(userId, 0, coin)
            if (wallet == null) {
                logger.warn("用户钱包不存在: userId=$userId, coin=$coin")
                return
            }

            // 根据盈亏情况更新钱包余额
            if (profitLoss.compareTo(BigDecimal.ZERO) > 0) {
                // 盈利：增加可用余额
                appUserWalletV2Service.addAvailableBalance(
                    userId = userId,
                    walletType = 0,
                    amount = profitLoss,
                    operationType = "STOCK_PROFIT",
                    remark = "股票平仓盈利: ${stock.name}(${stock.symbol}), $reason, 盈利金额: $profitLoss"
                )
                logger.info("钱包结算-盈利: 用户=$userId, 股票=${stock.symbol}, 盈利=$profitLoss")
            } else if (profitLoss.compareTo(BigDecimal.ZERO) < 0) {
                // 亏损：从可用余额中扣除
                val lossAmount = profitLoss.abs()
                appUserWalletV2Service.subtractAvailableBalance(
                    userId = userId,
                    walletType = 0,
                    amount = lossAmount,
                    operationType = "STOCK_LOSS",
                    remark = "股票平仓亏损: ${stock.name}(${stock.symbol}), $reason, 亏损金额: $lossAmount"
                )
                logger.info("钱包结算-亏损: 用户=$userId, 股票=${stock.symbol}, 亏损=$lossAmount")
            } else {
                // 盈亏为0：记录平仓操作
                appUserWalletV2Service.addAvailableBalance(
                    userId = userId,
                    walletType = 0,
                    amount = BigDecimal.ZERO,
                    operationType = "STOCK_CLOSE",
                    remark = "股票平仓: ${stock.name}(${stock.symbol}), $reason, 盈亏为0"
                )
                logger.info("钱包结算-平仓: 用户=$userId, 股票=${stock.symbol}, 盈亏为0")
            }

        } catch (e: Exception) {
            logger.error(e) { "钱包结算失败: userId=${position.userId}, 股票=${stock.symbol}, 盈亏=$profitLoss" }
        }
    }


    /**
     * 更新持仓缓存
     */
    private fun updatePositionCache(position: UserPosition, stock: Stock) {
        try {
            userPositionService.updatePositionCache(position, stock)

            logger.debug("调用 UserPositionServiceImpl.updatePositionCache 成功: positionId=${position.id}")

        } catch (e: Exception) {
            logger.error(e) { "调用 updatePositionCache 失败: positionId=${position.id}" }
        }
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
            logger.info("开始平仓: 用户=${position.userId}, 股票=${stock.symbol}, 价格=$closePrice, 原因=$reason")

            // 计算盈亏
            val profitLoss = calculateProfitLoss(position, closePrice)

            // 更新持仓状态
            position.status = "2" // 已平仓
            position.sellOrderTime = LocalDateTime.now()
            position.sellOrderPrice = closePrice
            position.profitAndLose = profitLoss
            position.allProfitAndLose = profitLoss

            // 保存更新
            userPositionService.updateById(position)

            // 钱包结算
            settleWalletBalance(position, stock, profitLoss, reason)

            // 清理止盈止损缓存
            clearProfitStopCache(position, stock)

            // 清理用户持仓缓存
            clearPositionCache(position, stock)

            logger.info("平仓完成: 用户=${position.userId}, 股票=${stock.symbol}, 盈亏=$profitLoss, 原因=$reason")

        } catch (e: Exception) {
            logger.error(e) { "平仓操作失败: 用户=${position.userId}, 股票=${stock.symbol}" }
        }
    }

    /**
     * 计算盈亏
     */
    private fun calculateProfitLoss(position: UserPosition, closePrice: BigDecimal): BigDecimal {
        val buyPrice = position.buyOrderPrice ?: BigDecimal.ZERO
        val orderNum = position.orderNum ?: BigDecimal.ZERO
        val lotUnit = position.lotUnit ?: 1
        val lever = position.orderLever ?: 1

        return when (position.orderDirection) {
            "买涨" -> {
                // 买涨：(卖出价 - 买入价) * 数量 * Lot单位 * 杠杆
                closePrice.subtract(buyPrice)
                    .multiply(orderNum)
                    .multiply(BigDecimal.valueOf(lotUnit.toLong()))
                    .multiply(BigDecimal.valueOf(lever.toLong()))
            }

            "买跌" -> {
                // 买跌：(买入价 - 卖出价) * 数量 * Lot单位 * 杠杆
                buyPrice.subtract(closePrice)
                    .multiply(orderNum)
                    .multiply(BigDecimal.valueOf(lotUnit.toLong()))
                    .multiply(BigDecimal.valueOf(lever.toLong()))
            }

            else -> BigDecimal.ZERO
        }.setScale(2, RoundingMode.HALF_UP)
    }


    /**
     * 清理止盈止损缓存
     */
    private fun clearProfitStopCache(position: UserPosition, stock: Stock) {
        try {
            val key = String.format(CHECK_ORDER_KEY, stock.flag + stock.symbol)
            val cacheMap = redissonClient.getMap<String, String>(key)

            // 清理止盈缓存
            position.id?.let { positionId ->
                cacheMap.remove("${positionId}2") // 止盈
                cacheMap.remove("${positionId}3") // 止损
            }

        } catch (e: Exception) {
            logger.error(e) { "清理止盈止损缓存失败: positionId=${position.id}" }
        }
    }

    /**
     * 清理用户持仓缓存（调用 UserPositionServiceImpl 的方法）
     */
    private fun clearPositionCache(position: UserPosition, stock: Stock) {
        try {
            userPositionService.clearPositionCache(position, stock)

            logger.debug("调用 UserPositionServiceImpl.clearPositionCache 成功: positionId=${position.id}")

        } catch (e: Exception) {
            logger.error(e) { "调用 clearPositionCache 失败: positionId=${position.id}" }
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