package com.fund.modules.stock

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.math.BigDecimal

/**
 * 股票下单请求参数
 */
class StockAddOrderRequest : Serializable {

    /**
     * 股票ID
     */
    @NotBlank(message = "股票ID不能为空")
    var stockId: String? = null

    /**
     * 股票类型/市场标识
     */
    @NotBlank(message = "股票类型不能为空")
    var stockType: String? = null

    /**
     * 买入数量
     */
    @NotNull(message = "买入数量不能为空")
    var buyNum: Int? = null

    /**
     * 买入类型
     */
    @NotNull(message = "买入类型不能为空")
    var buyType: Int? = null

    /**
     * 杠杆倍数
     */
    @NotNull(message = "杠杆倍数不能为空")
    var lever: Int? = null

    /**
     * 止盈价格（可选）
     */
    var profitTarget: BigDecimal? = null

    /**
     * 止损价格（可选）
     */
    var stopTarget: BigDecimal? = null

    /**
     * 目标价格（可选）
     */
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

