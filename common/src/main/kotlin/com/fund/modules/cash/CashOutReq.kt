package com.fund.modules.cash

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

class CashOutReq {

    /**
     * 提现金额, 最低1
     */
    @Min(value = 1, message = "cash_out_min")
    @NotNull(message = "cash_out_not_null")
    var amount:  BigDecimal? = null

    /**
     * 资金密码
     */
    @NotBlank(message = "money_password_not_empty")
    var moneyPassword:  String? = null

    @NotBlank(message = "coinType is required")
    var coinType: String? = null

    /**
     * 支付平台名称
     */
    var platformName: String? = null

    /**
     * 提现地址
     */
    var address: String? = null

}