package com.fund.modules.block

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@Schema(description = "大宗交易申购请求")
class BlockTradeApplyRequest: Serializable {

    @Schema(description = "大宗交易记录ID", required = true, example = "1")
    var blockTradeId: Long? = null

    @Schema(description = "申购数量", required = true, example = "100")
    var applyNums: Int? = null

}

