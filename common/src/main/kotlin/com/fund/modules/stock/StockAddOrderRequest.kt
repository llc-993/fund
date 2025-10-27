package com.fund.modules.stock

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.math.BigDecimal

/**
 * 股票下单请求参数
 */
@Schema(description = "股票挂单请求")
class StockAddOrderRequest : Serializable {

    @Schema(description = "股票ID", required = true, example = "AAPL")
    @NotBlank(message = "股票ID不能为空")
    var stockId: String? = null

    @Schema(description = "股票类型/市场标识", required = true, example = "US")
    @NotBlank(message = "股票类型不能为空")
    var stockType: String? = null

    @Schema(description = "买入数量", required = true, example = "10")
    @NotNull(message = "买入数量不能为空")
    var buyNum: Int? = null

    @Schema(description = "买入类型", required = true, example = "1")
    @NotNull(message = "买入类型不能为空")
    var buyType: Int? = null

    @Schema(description = "杠杆倍数", required = true, example = "2")
    @NotNull(message = "杠杆倍数不能为空")
    var lever: Int? = null

    @Schema(description = "止盈价格", example = "150.00")
    var profitTarget: BigDecimal? = null

    @Schema(description = "止损价格", example = "140.00")
    var stopTarget: BigDecimal? = null

    @Schema(description = "目标价格", example = "145.00")
    var targetPrice: BigDecimal? = null

    override fun toString(): String {
        return "StockAddOrderRequest{" +
                "stockId='" + stockId + '\'' +
                ", stockType='" + stockType + '\'' +
                ", buyNum=" + buyNum +
                ", buyType=" + buyType +
                ", lever=" + lever +
                ", profitTarget=" + profitTarget +
                ", stopTarget=" + stopTarget +
                ", targetPrice=" + targetPrice +
                '}'
    }
}

