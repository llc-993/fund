package com.fund.modules.aiquant.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "保存AI量化渠道（新增或修改）")
data class AiQuantChannelSaveReq(
    val id: Long? = null,
    val name: String,
    val stockId: Long? = null,
    val symbol: String,
    val market: String,
    val csLink: String,
    val sortOrder: Int = 0,
    val enable: Int = 1,
    val remark: String? = null,
)

@Schema(description = "渠道分页")
data class AiQuantChannelPageReq(
    val current: Long = 1,
    val size: Long = 50,
)
