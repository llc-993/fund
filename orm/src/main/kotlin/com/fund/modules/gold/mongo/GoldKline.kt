package com.fund.modules.gold.mongo

import org.springframework.data.annotation.Id
import java.math.BigDecimal

/**
 * 积存金行情 K 线文档。
 * 集合命名：gold_kline_{channelCode}_{interval}。
 * timestamp 为对齐到 interval 起始时刻的秒级时间戳。
 */
data class GoldKline(
    @Id
    val id: String? = null,
    /** 渠道ID */
    val channelId: Long,
    /** 渠道编码 */
    val channelCode: String,
    /** 粒度：1min / 5min / 30min / 1h / 1day / 1week / 1month */
    val interval: String,
    /** 桶起始时间戳（秒） */
    val timestamp: Long,
    /** 开盘价 */
    val open: BigDecimal,
    /** 最高价 */
    val high: BigDecimal,
    /** 最低价 */
    val low: BigDecimal,
    /** 收盘价 */
    val close: BigDecimal,
    /** 成交克数 */
    val volume: BigDecimal = BigDecimal.ZERO,
    /** 文档创建/最近更新时间（毫秒） */
    val createTime: Long = System.currentTimeMillis(),
)
