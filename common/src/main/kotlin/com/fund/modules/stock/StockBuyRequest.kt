package com.fund.modules.stock

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.io.Serializable
import java.math.BigDecimal

@Schema(description = "买入股票请求")
class StockBuyRequest: Serializable {

    @Schema(description = "股票代码", required = true, example = "AAPL")
    @NotBlank(message = "Stock is required")
    var stockId: String? = null

    @Schema(description = "买入数量", required = true, example = "10")
    @NotBlank(message = "buy_not_empty")
    var buyNum: BigDecimal? = null

    @Schema(description = "订单类型", example = "1")
    var buyType: Int? = null

    @Schema(description = "杠杆倍数", example = "2")
    var lever: Int? = null

    @Schema(description = "止盈价格", example = "150.00")
    var profitTarget: BigDecimal? = null

    @Schema(description = "止损价格", example = "140.00")
    var stopTarget: BigDecimal? = null
}