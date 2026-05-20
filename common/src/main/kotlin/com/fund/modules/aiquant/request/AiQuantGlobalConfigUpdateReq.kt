package com.fund.modules.aiquant.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "更新AI量化全局配置")
data class AiQuantGlobalConfigUpdateReq(
    val minReserveAmount: BigDecimal? = null,
    /** 手续费率小数，如 0.1 表示 10% */
    val feeRate: BigDecimal? = null,
    val replaceContractEntry: Int? = null,
)
