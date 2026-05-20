package com.fund.modules.aiquant.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

/** 完成前更新卖出价与时间等 */
@Schema(description = "管理端更新AI量化订单")
data class AiQuantOrderUpdateManageReq(
    @Schema(description = "订单ID", required = true)
    val orderId: Long,

    @Schema(description = "卖出时间")
    val sellTime: LocalDateTime? = null,

    @Schema(description = "卖出价")
    val sellPrice: BigDecimal? = null,

    @Schema(description = "备注")
    val remark: String? = null,
)
