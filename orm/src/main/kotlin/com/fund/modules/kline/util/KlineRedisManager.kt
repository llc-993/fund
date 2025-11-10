package com.fund.modules.kline.util

import com.alibaba.fastjson2.JSON
import com.fund.modules.kline.model.Kline
import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

/**
 * K线 Redis 管理器
 * 使用 SortedSet 存储K线数据
 */
@Component
class KlineRedisManager(
    private val redissonClient: RedissonClient
) {
    
    private val logger = KotlinLogging.logger {}
    
    companion object {
        private const val REDIS_KEY_PREFIX = "kline:"
        private const val MAX_REDIS_SIZE = 100
        private const val PERSIST_BATCH_SIZE = 50
    }
    
    /**
     * 获取 Redis Key
     */
    fun getRedisKey(market: String, symbol: String, interval: String): String {
        return "${REDIS_KEY_PREFIX}${market}_${symbol}_${interval}"
    }
    
    /**
     * 添加K线数据到 Redis SortedSet
     * @return 如果需要持久化，返回需要持久化的K线数据列表
     */
    fun addKline(kline: Kline): List<Kline>? {
        try {
            val key = getRedisKey(kline.market, kline.symbol, kline.interval)
            val sortedSet = redissonClient.getScoredSortedSet<String>(key)
            
            // 使用时间戳作为 score，K线JSON作为 value
            val klineJson = JSON.toJSONString(kline)
            sortedSet.add(kline.timestamp.toDouble(), klineJson)
            
            // 检查是否超过最大容量
            val size = sortedSet.size()
            if (size > MAX_REDIS_SIZE) {
                return persistOldData(key, sortedSet)
            }
            
            return null
        } catch (e: Exception) {
            logger.error(e) { "添加K线到Redis失败: ${kline.symbol}-${kline.interval}" }
            return null
        }
    }
    
    /**
     * 持久化旧数据
     * 获取最旧的50条数据，然后从Redis中删除
     */
    private fun persistOldData(key: String, sortedSet: org.redisson.api.RScoredSortedSet<String>): List<Kline> {
        try {
            val size = sortedSet.size()
            val toRemoveCount = size - MAX_REDIS_SIZE + PERSIST_BATCH_SIZE
            
            // 获取最旧的数据
            val oldDataJson = sortedSet.valueRange(0, toRemoveCount - 1)
            
            // 解析为 Kline 对象
            val klineList = oldDataJson.mapNotNull { json ->
                try {
                    JSON.parseObject(json, Kline::class.java)
                } catch (e: Exception) {
                    logger.error(e) { "解析K线JSON失败: $json" }
                    null
                }
            }
            
            // 从 Redis 中删除这些数据
            sortedSet.removeRangeByRank(0, toRemoveCount - 1)
            
            return klineList
        } catch (e: Exception) {
            logger.error(e) { "持久化旧数据失败: $key" }
            return emptyList()
        }
    }
    
    /**
     * 获取最新的K线数据
     */
    fun getLatestKline(market: String, symbol: String, interval: String): Kline? {
        try {
            val key = getRedisKey(market, symbol, interval)
            val sortedSet = redissonClient.getScoredSortedSet<String>(key)
            
            // 获取最后一条数据
            val lastJson = sortedSet.last()
            return lastJson?.let { JSON.parseObject(it, Kline::class.java) }
        } catch (e: Exception) {
            logger.error(e) { "获取最新K线失败: $market-$symbol-$interval" }
            return null
        }
    }
    
    /**
     * 获取指定范围的K线数据
     */
    fun getKlineRange(market: String, symbol: String, interval: String, limit: Int): List<Kline> {
        try {
            val key = getRedisKey(market, symbol, interval)
            val sortedSet = redissonClient.getScoredSortedSet<String>(key)
            
            val size = sortedSet.size()
            val startIndex = maxOf(0, size - limit)
            val dataJson = sortedSet.valueRange(startIndex, size - 1)
            
            return dataJson.mapNotNull { json ->
                try {
                    JSON.parseObject(json, Kline::class.java)
                } catch (e: Exception) {
                    logger.error(e) { "解析K线JSON失败" }
                    null
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "获取K线范围数据失败: $market-$symbol-$interval" }
            return emptyList()
        }
    }
}

