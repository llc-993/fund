package com.fund.modules.risingfalling

import com.fund.common.entity.PageReq
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@Schema(description = "后台涨跌板块查询请求参数")
class AdminRisingFallingSectorsQueryRequest : PageReq(), Serializable {

    @Schema(description = "交易对符号（精确匹配）", example = "AAPL", nullable = true)
    var symbol: String? = null

    @Schema(
        description = "股票锁定状态：0=未锁定，1=锁定",
        example = "0",
        allowableValues = ["0", "1"],
        nullable = true
    )
    var stockLockStatus: Int? = null

    @Schema(
        description = "显示状态：0=显示，1=隐藏",
        example = "0",
        allowableValues = ["0", "1"],
        nullable = true
    )
    var displayStatus: Int? = null
}
