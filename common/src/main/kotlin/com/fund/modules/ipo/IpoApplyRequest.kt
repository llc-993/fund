package com.fund.modules.ipo

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@Schema(description = "IPO申购请求")
class IpoApplyRequest: Serializable {

    @Schema(description = "IPO记录ID", required = true, example = "1")
    var ipoId: Long? = null

    @Schema(description = "申购数量", required = true, example = "100")
    var applyNums: Int? = null

}