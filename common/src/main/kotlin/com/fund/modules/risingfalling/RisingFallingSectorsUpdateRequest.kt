package com.fund.modules.risingfalling

import com.fund.common.entity.IdReq
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "更新涨跌板块申购请求")
class RisingFallingSectorsUpdateRequest: IdReq() {

    @Schema(description = "申购数量", required = true, example = "200")
    var applyNums: Int? = null

}
