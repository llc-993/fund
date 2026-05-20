package com.fund.modules.aiquant.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/** 管理端审核周期：通过则核定本金；驳回则全额解冻预约冻结 */
@Schema(description = "AI量化周期审核")
data class AiQuantAuditManageReq(
    @Schema(description = "周期ID", required = true)
    val cycleId: Long,

    @Schema(description = "true=通过 false=驳回", required = true)
    val passed: Boolean,

    /** 核定本金，通过时必填，且不大于预约金额 */
    @Schema(description = "核定本金（通过时必填）")
    val approvedAmount: BigDecimal? = null,

    @Schema(description = "驳回原因")
    val rejectReason: String? = null,
)
