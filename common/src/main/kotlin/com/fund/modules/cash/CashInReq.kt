package com.fund.modules.cash

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Schema(description = "充值请求")
class CashInReq {

    @Schema(description = "充值金额（最低1）", required = true, example = "1000")
    @Min(value = 1, message = "recharge_min")
    @NotNull(message = "recharge_amount_not_null")
    var amount: BigDecimal? = null

    @Schema(description = "图片链接")
    var imgUrl: String? = null

    @Schema(description = "充值币种", required = true, example="USD、INR、CNY")
    var depositCode: String = "INR"
}