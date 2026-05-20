package com.fund.modules.aiquant.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "管理端AI量化订单分页")
data class AiQuantOrderPageManageReq(
    val current: Long = 1,
    val size: Long = 20,
    val cycleId: Long? = null,
    val userId: Long? = null,
)
