package com.fund.modules.cash

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Schema(description = "提现请求")
class CashOutReq {

    @Schema(description = "提现金额（最低1）", required = true, example = "500")
    @Min(value = 1, message = "cash_out_min")
    @NotNull(message = "cash_out_not_null")
    var amount:  BigDecimal? = null

    @Schema(description = "资金密码", required = true, example = "123456")
    @NotBlank(message = "money_password_not_empty")
    var moneyPassword:  String? = null

    @Schema(description = "币种类型", required = true, example = "USD")
    @NotBlank(message = "coinType is required")
    var coinType: String? = null

    @Schema(description = "支付平台名称", example = "Bank")
    var platformName: String? = null

    @Schema(description = "提现地址", example = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb")
    var address: String? = null

}