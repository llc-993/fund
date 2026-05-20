package com.fund.modules.gold.service

import com.fund.modules.gold.model.AppGoldChannel
import com.fund.modules.gold.mongo.GoldKline
import java.math.BigDecimal

/** 积存金 K 线 Mongo 写入与查询 */
interface GoldKlineMongoService {
    /** 将一次行情扩散到所有粒度桶并 upsert */
    fun appendQuote(channel: AppGoldChannel, price: BigDecimal, quoteTimeMillis: Long)

    /** 最近 limit 条，按时间升序返回 */
    fun listLatest(channelCode: String, interval: String, limit: Int): List<GoldKline>
}
