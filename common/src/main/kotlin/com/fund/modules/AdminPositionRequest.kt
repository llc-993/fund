package com.fund.modules

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "后台持仓查询参数")
class AdminPositionRequest {

    @Schema(description = "用户账号", example = "user001", nullable = true)
    var account: String? = null

    @Schema(description = "用户昵称", example = "小明", nullable = true)
    var username: String? = null

    @Schema(description = "持仓状态（1=持仓，2=平仓中，3=已平仓，4=失败）", example = "1", nullable = true)
    var status: String? = null

    @Schema(description = "买入开始时间", example = "2025-11-01T00:00:00", nullable = true)
    var startTime: LocalDateTime? = null

    @Schema(description = "买入结束时间", example = "2025-11-30T23:59:59", nullable = true)
    var endTime: LocalDateTime? = null
}