package com.fund.modules

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "更新持仓锁仓状态请求")
class UpdateLockStatusRequest {

    @Schema(description = "持仓ID", example = "1001", required = true)
    var positionId: Int? = null

    @Schema(description = "锁仓状态（0=未锁仓，1=锁仓）", example = "1", required = true)
    var isLock: Byte? = null
}

