package com.fund.modules.gold.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "积存金全局配置更新")
data class GoldGlobalConfigUpdateReq(
    val defaultBuyFeeRate: BigDecimal? = null,
    val defaultSellFeeRate: BigDecimal? = null,
    val defaultMinBuyAmount: BigDecimal? = null,
    val defaultMinSellGrams: BigDecimal? = null,
    val defaultGramScale: Int? = null,
    val defaultPriceToleranceBps: Int? = null,
    val quoteCacheSeconds: Int? = null,
    val riskNoticeUrl: String? = null,
    val entryEnable: Int? = null,
)
