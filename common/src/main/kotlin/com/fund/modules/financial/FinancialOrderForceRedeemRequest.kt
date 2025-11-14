package com.fund.modules.financial

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 理财订单强制赎回请求
 */
@Schema(description = "理财订单强制赎回请求")
data class FinancialOrderForceRedeemRequest(
    @Schema(description = "订单ID", required = true, example = "1")
    val orderId: Long,
    
    @Schema(description = "备注", example = "管理员强制赎回")
    val remark: String? = null
)
