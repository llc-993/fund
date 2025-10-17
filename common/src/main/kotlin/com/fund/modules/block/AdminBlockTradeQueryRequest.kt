package com.fund.modules.block

import com.fund.common.entity.PageReq
import java.io.Serializable

class AdminBlockTradeQueryRequest : PageReq(), Serializable {

    /**
     * 股票名称
     */
    var name: String? = null

    /**
     * 状态
     */
    var status: Int? = null
}

