package com.fund.modules.risingFalling

import com.fund.common.entity.PageReq
import java.io.Serializable

class AdminRisingFallingSectorsQueryRequest : PageReq(), Serializable {

    /**
     * 交易对
     */
    var symbol: String? = null

    /**
     * 股票锁定状态
     */
    var stockLockStatus: Int? = null

    /**
     * 显示状态
     */
    var displayStatus: Int? = null
}
