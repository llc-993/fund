package com.fund.modules.financial.model;

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 理财订单
 * </p>
 *
 * @author 书记
 * @since 2025-10-27
 */
@Schema(description = "理财订单实体")
@TableName("financial_order")
class FinancialOrder : Serializable {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @TableField("user_id")
    var userId: Long? = null

    /**
     * 产品代码
     */
    @Schema(description = "产品代码")
    @TableField("product_code")
    var productCode: String? = null

    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    @TableField("product_name")
    var productName: String? = null

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    @TableField("order_no")
    var orderNo: String? = null

    /**
     * 订单类型(1-活期 2-定期)
     */
    @Schema(description = "订单类型(1-活期 2-定期)")
    @TableField("order_type")
    var orderType: String? = null

    /**
     * 活期年利率
     */
    @Schema(description = "活期年利率")
    @TableField("current_rate")
    var currentRate: BigDecimal? = null

    /**
     * 定期年利率(存json)
     */
    @Schema(description = "定期年利率(多语言JSON)")
    @TableField("term_rate")
    var termRate: String? = null

    /**
     * 申购时间
     */
    @Schema(description = "申购时间")
    @TableField("apply_time")
    var applyTime: LocalDateTime? = null

    /**
     * 计息开始时间
     */
    @Schema(description = "计息开始时间")
    @TableField("interest_start_day")
    var interestStartDay: LocalDateTime? = null

    /**
     * 计息结束时间
     */
    @Schema(description = "计息结束时间")
    @TableField("interest_end_day")
    var interestEndDay: LocalDateTime? = null

    /**
     * 最后计息时间
     */
    @Schema(description = "最后计息时间")
    @TableField("last_interest_day")
    var lastInterestDay: LocalDateTime? = null

    /**
     * 理财金额
     */
    @Schema(description = "理财金额")
    @TableField("amount")
    var amount: BigDecimal? = null

    /**
     * 账户类型
     */
    @Schema(description = "账户类型")
    @TableField("account_type")
    var accountType: String? = null

    /**
     * 昨日收益
     */
    @Schema(description = "昨日收益")
    @TableField("last_amount")
    var lastAmount: BigDecimal? = null

    /**
     * 累计收益
     */
    @Schema(description = "累计收益")
    @TableField("profit_amount")
    var profitAmount: BigDecimal? = null

    /**
     * 订单状态(1-生效中 2-已平仓 3-已过期)
     */
    @Schema(description = "订单状态(1-生效中 2-已平仓 3-已过期)")
    @TableField("order_status")
    var orderStatus: String? = null

    /**
     * 平仓时间
     */
    @Schema(description = "平仓时间")
    @TableField("sell_time")
    var sellTime: LocalDateTime? = null

    override fun toString(): String {
        return "FinancialOrder(id=$id, userId=$userId, productCode=$productCode, productName=$productName, orderNo=$orderNo, orderType=$orderType, currentRate=$currentRate, termRate=$termRate, applyTime=$applyTime, interestStartDay=$interestStartDay, interestEndDay=$interestEndDay, lastInterestDay=$lastInterestDay, amount=$amount, accountType=$accountType, lastAmount=$lastAmount, profitAmount=$profitAmount, orderStatus=$orderStatus, sellTime=$sellTime)"
    }

}
