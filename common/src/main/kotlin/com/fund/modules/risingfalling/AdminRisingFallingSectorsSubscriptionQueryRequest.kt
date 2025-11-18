package com.fund.modules.risingfalling

import com.fund.common.entity.PageReq
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@Schema(description = "后台涨跌板块申购查询请求参数")
class AdminRisingFallingSectorsSubscriptionQueryRequest : PageReq(), Serializable {

    @Schema(description = "股票名称（精确匹配）", example = "苹果公司", nullable = true)
    var name: String? = null

    @Schema(description = "交易对符号（精确匹配）", example = "AAPL", nullable = true)
    var symbol: String? = null

    @Schema(description = "用户ID", example = "10001", nullable = true)
    var userId: Long? = null

    @Schema(
        description = "申购状态：1=已申购，2=已取消，3=已确认，4=已转持仓",
        example = "1",
        allowableValues = ["1", "2", "3", "4"],
        nullable = true
    )
    var status: Int? = null
}
