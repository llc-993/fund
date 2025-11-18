package com.fund.modules.block

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 大宗交易修改请求
 */
class AdminBlockTradeUpdateRequest : Serializable {

    /**
     * 主键
     */
    var id: Int? = null

    /**
     * 股票名称
     */
    var name: String? = null

    /**
     * 股票id
     */
    var stockId: Long? = null

    /**
     * 最大买入数量
     */
    var maxAmount: BigDecimal? = null

    /**
     * 最小买入数量
     */
    var minAmount: BigDecimal? = null

    /**
     * 锁定状态，1:锁定，2:不锁定
     */
    var lockStatus: Int? = null

    /**
     * 状态，1:开放，2:关闭
     */
    var status: Int? = null

    /**
     * 折扣
     */
    var discount: BigDecimal? = null

    /**
     * 开始售卖时间
     */
    var startDateTime: LocalDateTime? = null

    /**
     * 结束售卖时间
     */
    var endDateTime: LocalDateTime? = null

    /**
     * 释放时间
     */
    var releaseLookTime: LocalDateTime? = null

    /**
     * 第一次释放比例
     */
    var firstReleaseLookRate: BigDecimal? = null

    /**
     * 第一次释放时间
     */
    var firstReleaseLookDateTime: LocalDateTime? = null
}

