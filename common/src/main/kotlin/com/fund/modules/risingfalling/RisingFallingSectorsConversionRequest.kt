package com.fund.modules.risingfalling

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.math.BigDecimal

/**
 * 涨跌板块转持仓请求
 */
@Schema(description = "涨跌板块转持仓请求参数，用于将涨跌板块申购记录转化为用户持仓")
class RisingFallingSectorsConversionRequest : Serializable {

    @Schema(
        description = "申购记录ID（必填）",
        example = "1001",
        required = true
    )
    var id: Long? = null

    @Schema(
        description = "确认数量（必填，必须大于0）。此数量将作为持仓数量，需支付金额 = 确认数量 * 购买价格",
        example = "100.00",
        required = true
    )
    var confirmQuantity: BigDecimal? = null
}
