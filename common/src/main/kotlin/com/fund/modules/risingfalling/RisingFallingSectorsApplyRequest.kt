package com.fund.modules.risingFalling

import java.io.Serializable

class RisingFallingSectorsApplyRequest: Serializable {

    var risingFallingSectorsId: Long? = null

    var applyNums: Int? = null

    /**
     * 交易密码
     */
    var tradePassword: String? = null

}
