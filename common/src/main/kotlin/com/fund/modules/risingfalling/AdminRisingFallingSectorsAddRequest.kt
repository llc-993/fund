package com.fund.modules.risingFalling

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 涨跌板块新增请求
 */
class AdminRisingFallingSectorsAddRequest : Serializable {

    /**
     * 交易对
     */
    var symbol: String? = null

    /**
     * stock 的id
     */
    var stockId: Long? = null

    /**
     * 股票锁定状态
     */
    var stockLockStatus: Int? = null

    /**
     * 显示状态(0:显示，1:隐藏)
     */
    var displayStatus: Int? = null

    /**
     * 开始时间
     */
    var openTime: LocalDateTime? = null

    /**
     * 结束时间
     */
    var endTime: LocalDateTime? = null

    /**
     * 开始售卖时间
     */
    var startSellTime: LocalDateTime? = null

    /**
     * 结束售卖的时间
     */
    var endSellTime: LocalDateTime? = null

    /**
     * 密码
     */
    var passWord: String? = null
}
