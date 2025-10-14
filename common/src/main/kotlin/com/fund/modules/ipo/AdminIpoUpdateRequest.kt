package com.fund.modules.ipo

import java.io.Serializable
import java.math.BigDecimal

/**
 * IPO修改请求
 */
class AdminIpoUpdateRequest : Serializable {

    /**
     * 主键
     */
    var id: Long? = null

    /**
     * 名字
     */
    var name: String? = null

    /**
     * 国家
     */
    var country: String? = null

    /**
     * 产品代码
     */
    var symbol: String? = null

    /**
     * 开始时间（时间戳）
     */
    var openDate: Long? = null

    /**
     * 结束时间（时间戳）
     */
    var closeDate: Long? = null

    /**
     * 上市时间（时间戳）
     */
    var listingDate: Long? = null

    /**
     * 价格
     */
    var price: BigDecimal? = null

    /**
     * 认购数量
     */
    var count: Long? = null

    /**
     * 交易所名称
     */
    var exchange: String? = null

    /**
     * 状态，1:认购中，2:结束
     */
    var status: Int? = null

    /**
     * 1: 新股， 2:线下配售
     */
    var type: Int? = 1
}

