package com.fund.modules.aiquant.request

import io.swagger.v3.oas.annotations.media.Schema

/** 周期分页筛选 */
@Schema(description = "管理端AI量化周期分页")
data class AiQuantCyclePageManageReq(
    val current: Long = 1,
    val size: Long = 20,
    val userId: Long? = null,
    /** 可选：阶段过滤 */
    val phase: Int? = null,
)
