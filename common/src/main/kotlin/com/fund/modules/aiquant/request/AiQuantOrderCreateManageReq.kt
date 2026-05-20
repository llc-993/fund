package com.fund.modules.aiquant.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

/** 审核通过后建展示订单，买入信息必填；卖出可在更新接口补齐 */
@Schema(description = "管理端创建AI量化订单")
data class AiQuantOrderCreateManageReq(
    @Schema(description = "周期ID", required = true)
    val cycleId: Long,

    @Schema(description = "股票ID", required = true)
    val stockId: Long,

    @Schema(description = "买入时间", required = true)
    val buyTime: LocalDateTime,

    @Schema(description = "买入价", required = true)
    val buyPrice: BigDecimal,

    @Schema(description = "备注")
    val remark: String? = null,
)
