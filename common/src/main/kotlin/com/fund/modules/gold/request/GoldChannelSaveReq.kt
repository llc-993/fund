package com.fund.modules.gold.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "保存积存金渠道（新增或修改）")
data class GoldChannelSaveReq(
    val id: Long? = null,
    val channelCode: String,
    val channelName: String,
    val bankName: String? = null,
    val accountLabel: String? = null,
    val accountTag: String? = null,
    val logoUrl: String? = null,
    val csLink: String? = null,
    val riskNoticeUrl: String? = null,
    val currencyCode: String? = "HKD",
    val buyFeeRate: BigDecimal? = BigDecimal.ZERO,
    val sellFeeRate: BigDecimal? = BigDecimal.ZERO,
    val minBuyAmount: BigDecimal? = BigDecimal.ZERO,
    val minSellGrams: BigDecimal? = BigDecimal.ZERO,
    val gramScale: Int? = 4,
    val priceToleranceBps: Int? = 100,
    val sortOrder: Int? = 0,
    val enableFlag: Int? = 1,
    val remark: String? = null,
)
