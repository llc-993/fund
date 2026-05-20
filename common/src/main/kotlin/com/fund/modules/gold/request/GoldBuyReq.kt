package com.fund.modules.gold.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "积存金买入请求")
data class GoldBuyReq(
    @Schema(description = "渠道ID", required = true)
    val channelId: Long,
    @Schema(description = "买入金额（HKD）", required = true)
    val amount: BigDecimal,
    @Schema(description = "前端展示的实时价（防错价比对）")
    val expectPrice: BigDecimal? = null,
    @Schema(description = "备注")
    val remark: String? = null,
)
