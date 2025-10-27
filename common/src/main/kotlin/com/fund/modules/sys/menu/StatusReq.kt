package com.fund.modules.sys.menu

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "修改状态请求参数")
class StatusReq {
    
    @Schema(description = "记录ID", required = true, example = "1")
    var id: @NotNull Long? = null

    @Schema(description = "状态（true=启用，false=停用）", required = true, example = "true")
    var status: @NotNull Boolean? = null
}
