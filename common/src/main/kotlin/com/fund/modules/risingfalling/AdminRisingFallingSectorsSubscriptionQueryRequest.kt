package com.fund.modules.risingFalling

import com.fund.common.entity.PageReq
import java.io.Serializable

class AdminRisingFallingSectorsSubscriptionQueryRequest : PageReq(), Serializable {

    /**
     * 股票名称
     */
    var name: String? = null

    /**
     * 交易对
     */
    var symbol: String? = null

    /**
     * 用户ID
     */
    var userId: Long? = null

    /**
     * 状态
     */
    var status: Int? = null
}
