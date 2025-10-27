package com.fund.modules.stock

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.io.Serializable
import java.math.BigDecimal

@Schema(description = "更新盈利目标请求")
class UpdateProfitTargetRequest : Serializable {

    @Schema(description = "持仓编号", required = true, example = "SN123456789")
    @NotBlank
    var positionSn: String? = null

    @Schema(description = "止盈价格", example = "150.00")
    var profitTarget: BigDecimal? = null

    @Schema(description = "止损价格", example = "140.00")
    var stopTarget: BigDecimal? = null

}