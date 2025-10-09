package com.fund.modules.stock

import jakarta.validation.constraints.NotBlank
import java.io.Serializable
import java.math.BigDecimal

class UpdateProfitTargetRequest : Serializable {

    @NotBlank
    var positionSn: String? = null

    var profitTarget: BigDecimal? = null

    var stopTarget: BigDecimal? = null

}