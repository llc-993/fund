package com.fund.modules.gold.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "积存金卖出请求")
data class GoldSellReq(
    @Schema(description = "渠道ID", required = true)
    val channelId: Long,
    @Schema(description = "卖出克数", required = true)
    val grams: BigDecimal,
    @Schema(description = "前端展示的实时价（防错价比对）")
    val expectPrice: BigDecimal? = null,
    @Schema(description = "备注")
    val remark: String? = null,
)
