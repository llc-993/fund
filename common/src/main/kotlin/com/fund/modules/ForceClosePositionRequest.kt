package com.fund.modules

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "强制平仓请求")
class ForceClosePositionRequest {

    @Schema(description = "id", example = "1", required = true)
    var id: Long? = null

    @Schema(description = "操作类型（0=手动平仓，1=止盈平仓，2=止损平仓）", example = "0", required = true)
    var doType: Int? = null

    @Schema(description = "操作动作类型", example = "force_close", required = true)
    var actionType: String? = null
}

