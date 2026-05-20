package com.fund.modules.gold.request

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "积存金订单分页查询")
data class GoldOrderPageReq(
    @Schema(description = "页码") var current: Long = 1,
    @Schema(description = "每页大小") var size: Long = 20,
    @Schema(description = "用户ID（管理端用）") var userId: Long? = null,
    @Schema(description = "渠道ID") var channelId: Long? = null,
    @Schema(description = "方向 1买入 2卖出") var direction: Int? = null,
    @Schema(description = "起始时间") var startTime: LocalDateTime? = null,
    @Schema(description = "结束时间") var endTime: LocalDateTime? = null,
)
