package com.fund.modules.kline.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

/**
 * K线数据模型
 * 集合名称格式: kline_{market}_{symbol}_{interval}
 * 例如: kline_US_AAPL_1min, kline_CN_000001_1day
 */
@Document(collection = "kline")
data class Kline(
    @Id
    val id: String? = null, // MongoDB文档ID
    
    val symbol: String, // 股票代码
    val market: String, // 市场标识
    val interval: String, // 时间间隔 (1min, 5min, 30min, 1h, 1day, 1week, 1month)
    val timestamp: Long, // 时间戳（秒）
    
    // OHLCV数据
    val open: BigDecimal, // 开盘价
    val high: BigDecimal, // 最高价
    val low: BigDecimal, // 最低价
    val close: BigDecimal, // 收盘价
    val volume: BigDecimal, // 成交量
    
    val createTime: Long = System.currentTimeMillis() // 创建时间

)