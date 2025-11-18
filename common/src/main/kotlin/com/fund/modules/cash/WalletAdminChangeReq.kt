package com.fund.modules.cash

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Schema(description = "管理后台上下分请求参数")
class WalletAdminChangeReq {
    @Schema(description = "会员id", required = true)
    var userId: @NotNull Long? = null

    @Schema(description = "操作类型 1:加款 -1:减款", required = true)
    var type: @NotNull Int? = null

    @Schema(description = "币种代码, USD,CNY,INR", required = true)
    var currencyCode: @NotNull String? = null

    @Schema(description = "金额", required = true)
    var amount: @NotNull @Min(value = 1) BigDecimal? = null

    @Schema(description = "备注")
    var remark: String? = null
}
