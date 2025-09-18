package com.fund.modules.stock

import jakarta.validation.constraints.NotBlank
import java.io.Serializable
import java.math.BigDecimal

class StockBuyRequest: Serializable {

    /**
     * 股票代码
     */
    @NotBlank(message = "Stock is required")
    var stockId: String? = null

    /**
     * 买入数量
     */
    @NotBlank(message = "buy_not_empty")
    var buyNum: BigDecimal? = null

    /**
     * 订单类型
     */
    var buyType: Int? = null
    /**
     * 杠杆倍数
     */
    var lever: Int? = null

    /**
     * 止盈价格
     */
    var profitTarget: BigDecimal? = null

    /**
     * 止损价格
     */
    var stopTarget: BigDecimal? = null
}