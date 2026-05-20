package com.fund.modules.gold.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

@Schema(description = "积存金行情写入")
data class GoldQuoteUpsertReq(
    @Schema(description = "渠道ID", required = true)
    val channelId: Long,
    @Schema(description = "实时价格（HKD/克）", required = true)
    val price: BigDecimal,
    @Schema(description = "前日收盘价；不传则沿用既有值")
    val prevClosePrice: BigDecimal? = null,
    @Schema(description = "0 休市 1 交易中 2 已收盘")
    val tradingStatus: Int? = null,
    @Schema(description = "行情时间，不传取服务端 now")
    val quoteTime: LocalDateTime? = null,
)
