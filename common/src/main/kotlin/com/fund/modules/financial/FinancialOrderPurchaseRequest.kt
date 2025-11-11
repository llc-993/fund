package com.fund.modules.financial

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/**
 * 理财产品申购请求
 */
@Schema(description = "理财产品申购请求")
data class FinancialOrderPurchaseRequest(
    @Schema(description = "产品ID", required = true, example = "1")
    val productId: Long,

    @Schema(description = "投资金额", required = true, example = "10000.00")
    val amount: BigDecimal,

    @Schema(description = "钱包类型：0-主钱包 1-交易钱包", example = "0")
    val walletType: Byte? = 0,

    @Schema(description = "备注", example = "投资理财产品")
    val remark: String? = null
)
