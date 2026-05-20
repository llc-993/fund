package com.fund.modules.aiquant.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/** 用户预约 AI 量化：冻结等额可用至 ai_quant_freeze */
@Schema(description = "AI量化预约请求")
data class AiQuantReserveReq(
    @Schema(description = "预约本金金额", required = true)
    val amount: BigDecimal,

    /** 币种，对应 app_user_wallet.currency_code */
    @Schema(description = "钱包币种编码，默认 CNY")
    val currencyCode: String? = null,
)
