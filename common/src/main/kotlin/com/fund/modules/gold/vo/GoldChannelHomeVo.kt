package com.fund.modules.gold.vo

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/** 首页渠道卡片（含实时价与涨跌信息） */
@Schema(description = "积存金首页渠道卡片")
data class GoldChannelHomeVo(
    val channelId: Long,
    val channelCode: String,
    val channelName: String,
    val bankName: String?,
    val accountLabel: String?,
    val accountTag: String?,
    val logoUrl: String?,
    val csLink: String?,
    val currencyCode: String,
    val gramScale: Int,
    val price: BigDecimal,
    val changeAmount: BigDecimal,
    val changePct: BigDecimal,
    val tradingStatus: Int,
    val intradayHigh: BigDecimal?,
    val intradayLow: BigDecimal?,
    val intradayOpen: BigDecimal?,
)
