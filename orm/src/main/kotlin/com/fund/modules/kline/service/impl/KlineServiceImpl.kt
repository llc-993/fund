package com.fund.modules.kline.service.impl

import com.fund.common.enums.KlineEnum
import com.fund.modules.kline.model.Kline
import com.fund.modules.kline.service.KlineService
import com.fund.modules.kline.util.KlineAggregator
import com.fund.modules.kline.util.KlineRedisManager
import com.fund.modules.quotation.service.UserQuotationControlService
import com.fund.modules.stock.model.Stock
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * K线数据服务实现类
 */
@Service
class KlineServiceImpl @Autowired constructor(
    private val mongoTemplate: MongoTemplate,
    private val klineRedisManager: KlineRedisManager,
    private val quotationControlService: UserQuotationControlService
) : KlineService {

    private val logger = KotlinLogging.logger {}

    override fun saveKlines(klines: List<Kline>) {
        try {
            if (klines.isEmpty()) {
                return
            }
            for (kline in klines) {
                val collectionName = "Kline_${kline.market}_${kline.symbol}_${kline.interval}"
                // 根据 id 查询，存在则更新，不存在则新增
                val query = Query(Criteria.where("id").`is`(kline.id))
                // 计算 endTime = timestamp + intervalTime (秒)
                val intervalTime = KlineEnum.fromInterval(kline.interval)?.intervalTime ?: 60L
                val endTime = kline.timestamp + intervalTime
                val update = Update()
                    .set("open", kline.open)
                    .set("high", kline.high)
                    .set("low", kline.low)
                    .set("close", kline.close)
                    .set("volume", kline.volume)
                    .set("createTime", kline.createTime)
                    .set("timestamp", kline.timestamp)
                    .set("endTime", endTime)
                    .setOnInsert("symbol", kline.symbol)
                    .setOnInsert("market", kline.market)
                    .setOnInsert("interval", kline.interval)
                mongoTemplate.upsert(query, update, collectionName)
            }
        } catch (e: Exception) {
            logger.error(e) { "Error saving kline data" }
        }
    }

    override fun getKlinesBySymbol(
        symbol: String, 
        market: String, 
        interval: String, 
        limit: Int
    ): List<Kline> {
        return getKlinesBySymbolForUser(symbol, market, interval, limit, null)
    }

    override fun getKlinesBySymbolForUser(
        symbol: String,
        market: String,
        interval: String,
        limit: Int,
        userId: Long?
    ): List<Kline> {
        try {
            val collectionName = "Kline_${market}_${symbol}_${interval}"
            val query = Query()
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(limit)
            val klines = mongoTemplate.find(query, Kline::class.java, collectionName)
            // 如果有用户ID，应用价格调控
            return applyQuotationControl(klines, symbol, market, userId)
        } catch (e: Exception) {
            logger.error(e) { "Error getting klines for $symbol-$market-$interval" }
            return emptyList()
        }
    }

    override fun getKlinesByTimeRange(
        symbol: String,
        market: String,
        interval: String,
        startTime: Long,
        endTime: Long
    ): List<Kline> {
        return getKlinesByTimeRangeForUser(symbol, market, interval, startTime, endTime, null)
    }

    override fun getKlinesByTimeRangeForUser(
        symbol: String,
        market: String,
        interval: String,
        startTime: Long,
        endTime: Long,
        userId: Long?
    ): List<Kline> {
        try {
            val collectionName = "Kline_${market}_${symbol}_${interval}"
            val query = Query(
                Criteria.where("timestamp").gte(startTime).lte(endTime)
            ).with(Sort.by(Sort.Direction.ASC, "timestamp"))
            val klines = mongoTemplate.find(query, Kline::class.java, collectionName)
            return applyQuotationControl(klines, symbol, market, userId)
        } catch (e: Exception) {
            logger.error(e) { "Error getting klines by time range for $symbol-$market-$interval" }
            return emptyList()
        }
    }

    /**
     * 根据用户调控配置调整K线价格
     * effectTime 落在 [timestamp, endTime) 范围内才修改
     * floating > 0 修改 high，floating < 0 修改 low
     */
    private fun applyQuotationControl(klines: List<Kline>, symbol: String, market: String, userId: Long?): List<Kline> {
        if (userId == null || klines.isEmpty()) return klines
        val control = quotationControlService.getActiveControl(userId, symbol, market) ?: return klines
        val floating = control.floating ?: return klines
        val effectTime = control.effectTime ?: return klines
        if (floating == BigDecimal.ZERO) return klines
        
        return klines.map { kline ->
            // 判断 effectTime 是否在 [timestamp, endTime) 范围内
            if (effectTime >= kline.timestamp && effectTime < kline.endTime) {
                if (floating > BigDecimal.ZERO) {
                    // 正数修改 high
                    kline.copy(high = kline.high.add(floating))
                } else {
                    // 负数修改 low
                    kline.copy(low = kline.low.add(floating))
                }
            } else {
                kline
            }
        }
    }

    override fun deleteExpiredKlines(beforeTimestamp: Long) {
        try {
            // 获取所有K线相关的集合
            val collectionNames = mongoTemplate.collectionNames.filter { 
                it.startsWith("kline_") 
            }
            
            var totalDeleted = 0L
            collectionNames.forEach { collectionName ->
                try {
                    val query = Query(
                        Criteria.where("timestamp").lt(beforeTimestamp)
                    )
                    
                    val result = mongoTemplate.remove(query, Kline::class.java, collectionName)
                    totalDeleted += result.deletedCount
                    
                } catch (e: Exception) {
                    logger.error(e) { "Error deleting expired klines from collection: $collectionName" }
                }
            }
            
            logger.info("Deleted $totalDeleted expired kline records from ${collectionNames.size} collections")
            
        } catch (e: Exception) {
            logger.error(e) { "Error deleting expired klines" }
        }
    }
    
    override fun processKlineMessage(stock: Stock) {
        try {
            // 验证股票数据完整性
            if (stock.symbol.isNullOrEmpty() || stock.flag.isNullOrEmpty()) {
                logger.warn("股票数据不完整，跳过K线生成: symbol=${stock.symbol}, flag=${stock.flag}")
                return
            }
            
            val market = stock.flag!!
            val symbol = stock.symbol!!
            val currentTime = System.currentTimeMillis()
            
            // 获取时区
            val zoneId = KlineAggregator.getTimeZoneByMarket(market)
            
            // 为所有时间周期生成K线
            KlineAggregator.getAllIntervals().forEach { interval ->
                processKlineForInterval(stock, market, symbol, interval, currentTime, zoneId)
            }
            
        } catch (e: Exception) {
            logger.error(e) { "处理K线数据时发生错误: symbol=${stock.symbol}" }
        }
    }
    
    /**
     * 为指定时间周期处理K线
     */
    private fun processKlineForInterval(
        stock: Stock,
        market: String,
        symbol: String,
        interval: String,
        currentTime: Long,
        zoneId: java.time.ZoneId
    ) {
        try {
            // 对齐时间戳
            val alignedTimestamp = KlineAggregator.alignTimestamp(currentTime, interval, zoneId)
            
            // 获取当前周期的最新K线
            val latestKline = klineRedisManager.getLatestKline(market, symbol, interval)
            
            val kline = if (latestKline != null && latestKline.timestamp == alignedTimestamp) {
                // 更新现有K线
                updateKline(latestKline, stock)
            } else {
                // 创建新K线
                createNewKline(stock, market, symbol, interval, alignedTimestamp)
            }
            
            // 添加到 Redis，如果返回需要持久化的数据，则保存到 MongoDB
            val klinesToPersist = klineRedisManager.addKline(kline)
            klinesToPersist?.let { klineList ->
                if (klineList.isNotEmpty()) {
                    persistToMongoDB(klineList, market, symbol, interval)
                }
            }
            
        } catch (e: Exception) {
            logger.error(e) { "处理K线周期失败: $market-$symbol-$interval" }
        }
    }
    
    /**
     * 创建新K线
     */
    private fun createNewKline(
        stock: Stock,
        market: String,
        symbol: String,
        interval: String,
        timestamp: Long
    ): Kline {
        val price = stock.last ?: BigDecimal.ZERO
        val volume: Any = stock.volume ?: BigDecimal.ZERO
        
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
            volume = BigDecimal.valueOf(volume as Long) ,
            createTime = System.currentTimeMillis()
        )
    }
    
    /**
     * 更新现有K线
     */
    private fun updateKline(existingKline: Kline, stock: Stock): Kline {
        val currentPrice = stock.last ?: BigDecimal.ZERO
        val currentVolume: Any? = stock.volume ?: BigDecimal.ZERO
        
        return existingKline.copy(
            high = maxOf(existingKline.high, currentPrice),
            low = minOf(existingKline.low, currentPrice),
            close = currentPrice,
            volume = existingKline.volume.add(BigDecimal.valueOf(currentVolume as Long) ),
            createTime = System.currentTimeMillis()
        )
    }
    
    /**
     * 持久化K线数据到 MongoDB
     */
    private fun persistToMongoDB(klineList: List<Kline>, market: String, symbol: String, interval: String) {
        try {
            val collectionName = "Kline_${market}_${symbol}_${interval}"
            
            klineList.forEach { kline ->
                try {
                    mongoTemplate.insert(kline, collectionName)
                } catch (e: Exception) {
                    logger.error(e) { "插入K线到MongoDB失败: $collectionName, timestamp=${kline.timestamp}" }
                }
            }
            
            logger.info("成功持久化 ${klineList.size} 条K线到MongoDB: $collectionName")
            
        } catch (e: Exception) {
            logger.error(e) { "批量持久化K线失败: $market-$symbol-$interval" }
        }
    }
}