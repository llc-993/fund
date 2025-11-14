package com.fund.modules.financial

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 理财产品赎回请求
 */
@Schema(description = "理财产品赎回请求")
data class FinancialOrderRedeemRequest(
    @Schema(description = "订单ID", required = true, example = "1")
    val orderId: Long,

    @Schema(description = "备注", example = "提前赎回理财产品")
    val remark: String? = null
)
