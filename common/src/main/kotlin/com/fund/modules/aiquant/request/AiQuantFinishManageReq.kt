package com.fund.modules.aiquant.request

import io.swagger.v3.oas.annotations.media.Schema

/** 管理端完成周期：释放本金、结算盈亏与手续费，订单对用户可见 */
@Schema(description = "AI量化周期完成")
data class AiQuantFinishManageReq(
    @Schema(description = "周期ID", required = true)
    val cycleId: Long,
)
