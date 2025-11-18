package com.fund.modules.ipo

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.math.BigDecimal

/**
 * 新股申购转化请求
 * 
 * 用于将IPO中签记录转化为用户持仓
 * 
 * 业务说明：
 * - 根据中签数量和buyPrice计算需要支付的金额
 * - 从用户钱包扣除相应金额
 * - 如果余额不足，将申购记录状态改为未中签(status=2)
 * - 如果余额充足，创建UserPosition持仓记录，申购记录状态改为已转持仓(status=5)
 */
@Schema(description = "新股申购转化请求参数，用于将IPO中签记录转化为用户持仓")
class SubscriptionConversionRequest : Serializable {

    @Schema(
        description = "申购记录ID（必填）",
        example = "1001",
        required = true
    )
    var id: Long? = null

    @Schema(
        description = "中签数量（必填，必须大于0）。此数量将作为持仓数量，需支付金额 = 中签数量 * 购买价格",
        example = "100.00",
        required = true
    )
    var allotmentQuantity: BigDecimal? = null
}

