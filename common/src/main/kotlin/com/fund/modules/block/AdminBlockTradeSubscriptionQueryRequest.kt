package com.fund.modules.block

import com.fund.common.entity.PageReq
import java.io.Serializable

class AdminBlockTradeSubscriptionQueryRequest : PageReq(), Serializable {

    /**
     * 股票名称
     */
    var name: String? = null

    /**
     * 用户ID
     */
    var userId: Long? = null

    /**
     * 状态
     */
    var status: Int? = null
}

