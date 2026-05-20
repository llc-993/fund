package com.fund.modules.gold.serviceImpl

import com.fund.exception.BusinessException
import com.fund.modules.gold.model.AppGoldChannel
import com.fund.modules.gold.mongo.GoldKline
import com.fund.modules.gold.service.GoldKlineMongoService
import com.fund.modules.kline.util.KlineAggregator
import mu.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.ZoneId

@Service
open class GoldKlineMongoServiceImpl(
    private val mongoTemplate: MongoTemplate,
) : GoldKlineMongoService, InitializingBean {

    private val logger = KotlinLogging.logger {}

    companion object {
        /** HKD 业务对齐香港时区 */
        val HK_ZONE: ZoneId = ZoneId.of("Asia/Hong_Kong")
    }

    override fun afterPropertiesSet() {
        try {
            val intervals = KlineAggregator.getAllIntervals()
            mongoTemplate.collectionNames
                .filter { it.startsWith("gold_kline_") }
                .filter { cn -> intervals.any { cn.endsWith("_$it") } }
                .forEach { ensureIndex(it) }
        } catch (e: Exception) {
            logger.warn(e) { "积存金K线索引预热跳过" }
        }
    }

    private fun ensureIndex(collectionName: String) {
        try {
            val indexOps = mongoTemplate.indexOps(collectionName)
            indexOps.ensureIndex(
                Index().on("timestamp", Sort.Direction.ASC).unique().named("uk_timestamp"),
            )
        } catch (e: Exception) {
            logger.warn(e) { "创建索引失败: $collectionName" }
        }
    }

    override fun appendQuote(channel: AppGoldChannel, price: BigDecimal, quoteTimeMillis: Long) {
        val cc = channel.channelCode ?: throw BusinessException("渠道编码缺失")
        KlineAggregator.getAllIntervals().forEach { interval ->
            val ts = KlineAggregator.alignTimestamp(quoteTimeMillis, interval, HK_ZONE)
            val collection = "gold_kline_${cc}_$interval"
            val query = Query(Criteria.where("timestamp").`is`(ts))
            val update = Update()
                .setOnInsert("channelId", channel.id)
                .setOnInsert("channelCode", cc)
                .setOnInsert("interval", interval)
                .setOnInsert("timestamp", ts)
                .setOnInsert("open", price)
                .setOnInsert("volume", BigDecimal.ZERO)
                .max("high", price)
                .min("low", price)
                .set("close", price)
                .set("createTime", System.currentTimeMillis())
            try {
                mongoTemplate.upsert(query, update, GoldKline::class.java, collection)
                ensureIndex(collection)
            } catch (e: Exception) {
                logger.error(e) { "写入金价K线失败 channel=$cc interval=$interval ts=$ts" }
            }
        }
    }

    override fun listLatest(channelCode: String, interval: String, limit: Int): List<GoldKline> {
        val collection = "gold_kline_${channelCode}_$interval"
        val q = Query()
            .with(Sort.by(Sort.Direction.DESC, "timestamp"))
            .limit(limit)
        return try {
            val list = mongoTemplate.find(q, GoldKline::class.java, collection)
            list.sortedBy { it.timestamp }
        } catch (e: Exception) {
            logger.warn(e) { "查询K线失败 $collection" }
            emptyList()
        }
    }
}
