package com.fund.modules.risingfalling

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@Schema(description = "涨跌板块申购请求")
class RisingFallingSectorsApplyRequest: Serializable {

    @Schema(description = "涨跌板块记录ID", required = true, example = "1")
    var risingFallingSectorsId: Long? = null

    @Schema(description = "申购数量", required = true, example = "100")
    var applyNums: Int? = null

    @Schema(description = "交易密码", required = true, example = "123456")
    var tradePassword: String? = null

}
