package com.fund.modules.kline.job

import com.fund.modules.kline.model.Kline
import com.fund.modules.kline.util.KlineAggregator
import com.fund.modules.kline.util.KlineRedisManager
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.service.UserPositionService
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * K线数据填充定时任务
 * 用于填充交易时间内没有实时数据的股票K线
 */
@Component
class KlineFillJob(
    private val stockService: StockService,
    private val klineRedisManager: KlineRedisManager,
    private val userPositionService: UserPositionService
) {
    
    private val logger = KotlinLogging.logger {}
    
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    @Value("\${kline.fill.enabled:true}")
    private val fillEnabled: Boolean = true
    
    @Value("\${kline.fill.batch-size:50}")
    private val batchSize: Int = 50
    
    /**
     * 每分钟执行一次K线填充
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 */1 * * * ?")
    fun fillKlineData() {
        if (!fillEnabled) {
            return
        }
        
        try {
            logger.info("开始执行K线数据填充任务")
            val startTime = System.currentTimeMillis()
            
            // 获取所有活跃的股票
            val stocks = stockService.list()
            if (stocks.isEmpty()) {
                logger.info("没有找到活跃的股票数据")
                return
            }
            
            // 使用协程并发处理
            runBlocking {
                stocks.chunked(batchSize).forEach { batch ->
                    batch.map { stock ->
                        coroutineScope.launch {
                            processStockFill(stock)
                        }
                    }
                }
            }
            
            val duration = System.currentTimeMillis() - startTime
            logger.info("K线数据填充任务完成，处理 ${stocks.size} 只股票，耗时 ${duration}ms")
            
        } catch (e: Exception) {
            logger.error(e) { "K线数据填充任务执行失败" }
        }
    }
    
    /**
     * 处理单个股票的K线填充
     */
    private suspend fun processStockFill(stock: Stock) {
        withContext(Dispatchers.IO) {
            try {
                // 验证股票数据
                if (stock.symbol.isNullOrEmpty() || stock.flag.isNullOrEmpty()) {
                    return@withContext
                }
                
                val market = stock.flag!!
                val symbol = stock.symbol!!
                
                // 判断是否在交易时间内
                if (!userPositionService.isTradingTime(market)) {
                    return@withContext
                }
                
                // 获取时区
                val zoneId = KlineAggregator.getTimeZoneByMarket(market)
                val currentTime = System.currentTimeMillis()
                
                // 只处理 1min 周期（其他周期会自动聚合）
                val interval = "1min"
                
                // 对齐当前时间
                val alignedTimestamp = KlineAggregator.alignTimestamp(currentTime, interval, zoneId)
                
                // 检查当前周期是否已有数据
                val latestKline = klineRedisManager.getLatestKline(market, symbol, interval)
                
                // 如果当前周期已有数据，不需要填充
                if (latestKline != null && latestKline.timestamp == alignedTimestamp) {
                    return@withContext
                }
                
                // 获取上一个周期的数据
                val previousKline = if (latestKline != null) {
                    latestKline
                } else {
                    // 如果 Redis 中没有数据，尝试从数据库或使用股票当前价格
                    createDefaultKline(stock, market, symbol, interval, alignedTimestamp)
                }
                
                // 复制上一条数据作为当前周期的数据
                val newKline = fillKlineFromPrevious(previousKline, alignedTimestamp)
                
                // 添加到 Redis
                klineRedisManager.addKline(newKline)
                
            } catch (e: Exception) {
                logger.error(e) { "处理股票K线填充失败: ${stock.symbol}" }
            }
        }
    }
    
    /**
     * 从上一条K线填充当前K线
     */
    private fun fillKlineFromPrevious(previousKline: Kline, newTimestamp: Long): Kline {
        return previousKline.copy(
            id = newTimestamp.toString(),
            timestamp = newTimestamp,
            open = previousKline.close,     // 开盘价等于上一根的收盘价
            high = previousKline.close,     // 最高价等于收盘价
            low = previousKline.close,      // 最低价等于收盘价
            close = previousKline.close,    // 收盘价保持不变
            volume = BigDecimal.ZERO,       // 成交量为0
            createTime = System.currentTimeMillis()
        )
    }
    
    /**
     * 创建默认K线（当没有历史数据时）
     */
    private fun createDefaultKline(
        stock: Stock,
        market: String,
        symbol: String,
        interval: String,
        timestamp: Long
    ): Kline {
        val price = stock.last ?: BigDecimal.ZERO
        
        return Kline(
            id = timestamp.toString(),
            symbol = symbol,
            market = market,
            interval = interval,
            timestamp = timestamp,
            open = price,
            high = price,
            low = price,
            close = price,
            volume = BigDecimal.ZERO,
            createTime = System.currentTimeMillis()
        )
    }

}

