package com.fund.modules.gold

import com.fund.modules.gold.model.AppGoldPosition
import java.math.BigDecimal

/** 持仓变更计算结果（买入/卖出后） */
data class GoldPositionApplyResult(
    val costAvgBefore: BigDecimal,
    val costAvgAfter: BigDecimal,
    val sellCost: BigDecimal,
    val realizedProfit: BigDecimal,
    val position: AppGoldPosition,
)
