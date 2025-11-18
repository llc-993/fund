package com.fund.modules.cash

import jakarta.validation.constraints.NotNull
import java.io.Serializable

class CashOutEditReq : Serializable{

    /**
     * 提现申请id
     */
    var id: @NotNull Long? = null

    /**
     * 钱包地址
     */
    var address: String? = null

}