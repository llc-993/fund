package com.fund.modules.gold.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "积存金渠道分页查询")
data class GoldChannelPageReq(
    val current: Long = 1,
    val size: Long = 50,
    val enableFlag: Int? = null,
    val channelCode: String? = null,
)
