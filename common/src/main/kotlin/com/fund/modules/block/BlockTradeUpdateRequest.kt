package com.fund.modules.block

import com.fund.common.entity.IdReq
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "更新大宗交易申购请求")
class BlockTradeUpdateRequest: IdReq() {

    @Schema(description = "申购数量", required = true, example = "200")
    var applyNums: Int? = null

}

