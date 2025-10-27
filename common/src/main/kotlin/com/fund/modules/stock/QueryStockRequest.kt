package com.fund.modules.stock

import com.fund.common.entity.PageReq
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "查询股票请求")
class QueryStockRequest: PageReq() {

    @Schema(description = "股票代码/符号", example = "AAPL")
    var symbol: String? = null

    @Schema(description = "标志", example = "US")
    var flag: String? = null

}