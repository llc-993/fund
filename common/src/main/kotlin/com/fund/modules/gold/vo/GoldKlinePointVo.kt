package com.fund.modules.gold.vo

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/** K 线点位（与 MongoDB 文档字段对齐） */
@Schema(description = "积存金K线点位")
data class GoldKlinePointVo(
    val timestamp: Long,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
)
