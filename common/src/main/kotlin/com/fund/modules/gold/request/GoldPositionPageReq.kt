package com.fund.modules.gold.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "积存金持仓分页查询")
data class GoldPositionPageReq(
    val current: Long = 1,
    val size: Long = 20,
    val userId: Long? = null,
    val channelId: Long? = null,
)
