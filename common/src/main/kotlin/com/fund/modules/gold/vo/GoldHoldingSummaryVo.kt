package com.fund.modules.gold.vo

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/** 多渠道持仓汇总（黄金持仓总览页） */
@Schema(description = "积存金多渠道持仓汇总")
data class GoldHoldingSummaryVo(
    val totalHoldGrams: BigDecimal,
    val totalHoldValue: BigDecimal,
    val totalHoldingProfit: BigDecimal,
    val totalCumulativeProfit: BigDecimal,
    val currencyCode: String,
    val items: List<GoldPositionDetailVo>,
)
