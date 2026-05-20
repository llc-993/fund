package com.fund.modules.aiquant

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/** 用户端汇总：取自钱包维度统计与在途周期数 */
@Schema(description = "AI量化汇总")
data class AiQuantUserSummaryVo(
    val aiQuantFreeze: BigDecimal?,
    val aiQuantTotalInvest: BigDecimal?,
    val aiQuantTotalProfit: BigDecimal?,
    val aiQuantTotalFee: BigDecimal?,
    val activeReserveCount: Int,
)
