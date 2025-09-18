package com.fund.modules.kline.service.impl

import com.fund.modules.kline.model.Kline
import com.fund.modules.kline.service.KlineService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

/**
 * K线数据服务实现类
 */
@Service
class KlineServiceImpl @Autowired constructor(
    private val mongoTemplate: MongoTemplate
) : KlineService {

    private val logger = KotlinLogging.logger {}

    override fun saveKlines(klines: List<Kline>) {
        try {
            if (klines.isEmpty()) {
                return
            }

            for (kline in klines) {
                val collectionName = "Kline_${kline.market}_${kline.symbol}_${kline.interval}"
                mongoTemplate.insert(kline, collectionName)
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
        try {
            val collectionName = "Kline_${market}_${symbol}_${interval}"
            val query = Query()
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(limit)
            
            return mongoTemplate.find(query, Kline::class.java, collectionName)
            
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
        try {
            val collectionName = "Kline_${market}_${symbol}_${interval}"
            val query = Query(
                Criteria.where("timestamp").gte(startTime).lte(endTime)
            )
                .with(Sort.by(Sort.Direction.ASC, "timestamp"))
            
            return mongoTemplate.find(query, Kline::class.java, collectionName)
            
        } catch (e: Exception) {
            logger.error(e) { "Error getting klines by time range for $symbol-$market-$interval" }
            return emptyList()
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
}