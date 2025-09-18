package com.fund.modules.kline

import org.springframework.data.annotation.Id
import java.math.BigDecimal

class Kline {
    @Id
    var id:Long? = null

    var symbol: String? = null

    var period: String? = null

    /**
     * 数据来源
     */
    var dataSource: String? = null

    /**
     * 时间
     */
    var dateTimeStr: String? = null

    /**
     * 交易额
     */
    var volume: BigDecimal? = null

    /**
     * 交易次数
     */
    var count: Int? = null

    /**
     * 开盘价
     */
    var open: BigDecimal? = null

    /**
     * 收盘价
     */
    var close: BigDecimal? = null


    /**
     * 最低价
     */
    var low: BigDecimal? = null

    /**
     * 最高价
     */
    var high: BigDecimal? = null

    /**
     * 交易量
     */
    var vol: BigDecimal? = null

    /**
     * 数据来源
     */
    var source: Int? = 0
}