package com.fund.modules.cash

import jakarta.validation.constraints.NotNull
import java.io.Serializable

/**
 * 充值审核请求对象
 */
class CashInReviewReq: Serializable {
    /**
     * 申请id
     */
    var id: @NotNull Long? = null

    /**
     * 是否受理
     */
    var pass: @NotNull Boolean? = null

    /**
     * 充值的币种
     */
    var depositCode: String? = null

    /**
     * 失败原因,如果有
     */
    var reason: String? = null
}