package com.fund.modules.financial

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 理财产品下架请求
 */
@Schema(description = "理财产品下架请求")
data class FinancialProductOfflineRequest(
    @Schema(description = "产品ID", required = true, example = "1")
    val id: Long,
    
    @Schema(description = "是否强制赎回所有订单", example = "true")
    val forceRedeemAllOrders: Boolean = false,
    
    @Schema(description = "下架原因", example = "产品到期下架")
    val remark: String? = null
)
