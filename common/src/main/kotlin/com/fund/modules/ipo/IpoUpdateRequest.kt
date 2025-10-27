package com.fund.modules.ipo

import com.fund.common.entity.IdReq
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "更新IPO申购请求")
class IpoUpdateRequest: IdReq() {

    @Schema(description = "申购数量", required = true, example = "200")
    var applyNums: Int? = null

}