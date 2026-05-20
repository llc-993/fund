package com.fund.modules.gold.vo

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/** 单渠道持仓详情（详情页/单卡片） */
@Schema(description = "积存金渠道持仓详情")
data class GoldPositionDetailVo(
    val channelId: Long,
    val channelCode: String,
    val channelName: String,
    val accountLabel: String?,
    val accountTag: String?,
    val logoUrl: String?,
    val csLink: String?,
    val currencyCode: String,
    val holdGrams: BigDecimal,
    val holdValue: BigDecimal,
    val costAvgPrice: BigDecimal,
    val holdingProfit: BigDecimal,
    val cumulativeProfit: BigDecimal,
    val todayProfit: BigDecimal,
    val price: BigDecimal,
    val changeAmount: BigDecimal,
    val changePct: BigDecimal,
    val tradingStatus: Int,
    val intradayHigh: BigDecimal?,
    val intradayLow: BigDecimal?,
    val gramScale: Int,
)
