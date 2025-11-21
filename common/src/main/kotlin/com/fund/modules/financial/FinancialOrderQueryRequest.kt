package com.fund.modules.financial

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 理财订单查询请求
 */
@Schema(description = "理财订单查询请求")
data class FinancialOrderQueryRequest(
    @Schema(description = "页码", example = "1")
    val pageNum: Int = 1,

    @Schema(description = "每页数量", example = "10")
    val pageSize: Int = 10,

    @Schema(description = "用户ID")
    val userId: Long? = null,

    @Schema(description = "产品ID")
    val productId: Long? = null,

    @Schema(description = "订单状态：1-生效中 2-已平仓 3-已过期")
    val orderStatus: Byte? = null,

    @Schema(description = "产品编码")
    val productCode: String? = null,

    @Schema(description = "产品名称")
    val productName: String? = null
)
