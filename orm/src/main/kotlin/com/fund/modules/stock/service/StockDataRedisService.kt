package com.fund.modules.stock.service

import com.alibaba.fastjson2.JSON
import com.fund.common.RedisKeys
import com.fund.modules.stock.model.StockDataModel
import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service

/**
 * StockData Redis存储服务
 * 专门管理StockData的完整字段存储到Redis
 */
@Service
class StockDataRedisService(
    private val redissonClient: RedissonClient
) {
    
    private val logger = KotlinLogging.logger {}
    
    /**
     * 存储StockData到Redis
     * @param stockData StockDataModel对象
     * @param stockId 对应的Stock ID
     */
    fun saveStockData(stockData: StockDataModel, stockId: Long) {
        try {
            val key = getStockDataKey(stockId)
            val bucket = redissonClient.getBucket<String>(key)
            bucket.set(JSON.toJSONString(stockData))
            logger.debug("Saved StockData to Redis: stockId=$stockId, symbol=${stockData.code}")
        } catch (e: Exception) {
            logger.error(e) { "Error saving StockData to Redis: stockId=$stockId" }
        }
    }
    
    /**
     * 从Redis获取StockData
     * @param stockId Stock ID
     * @return StockDataModel对象，如果不存在返回null
     */
    fun getStockData(stockId: Long): StockDataModel? {
        return try {
            val key = getStockDataKey(stockId)
            val bucket = redissonClient.getBucket<String>(key)
            if (bucket.isExists) {
                val json = bucket.get()
                JSON.parseObject(json, StockDataModel::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error(e) { "Error getting StockData from Redis: stockId=$stockId" }
            null
        }
    }
    
    
    /**
     * 获取StockData的Redis key
     * @param stockId Stock ID
     * @return Redis key
     */
    private fun getStockDataKey(stockId: Long): String {
        return "${RedisKeys.STOCK_DATA_KEY}$stockId"
    }
}
