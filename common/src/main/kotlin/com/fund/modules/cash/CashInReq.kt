package com.fund.modules.cash

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

class CashInReq {

    @Min(value = 1, message = "recharge_min")
    @NotNull(message = "recharge_amount_not_null")
    var amount: BigDecimal? = null
}