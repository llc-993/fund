package com.fund.modules.aiquant.model

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * AI 量化展示订单（股票代码标的），完成后 user_visible=1 用户可见。
 */
@TableName("app_ai_quant_order")
class AppAiQuantOrder : Serializable {

    @TableId(type = IdType.AUTO)
    var id: Long? = null

    @TableField("cycle_id")
    var cycleId: Long? = null

    @TableField("user_id")
    var userId: Long? = null

    @TableField("order_no")
    var orderNo: String? = null

    @TableField("stock_id")
    var stockId: Long? = null

    @TableField("stock_name")
    var stockName: String? = null

    @TableField("symbol")
    var symbol: String? = null

    @TableField("market")
    var market: String? = null

    @TableField("buy_time")
    var buyTime: LocalDateTime? = null

    @TableField("sell_time")
    var sellTime: LocalDateTime? = null

    @TableField("buy_price")
    var buyPrice: BigDecimal? = null

    @TableField("sell_price")
    var sellPrice: BigDecimal? = null

    @TableField("buy_amount")
    var buyAmount: BigDecimal? = null

    @TableField("position_qty")
    var positionQty: BigDecimal? = null

    /** 订单毛利盈亏（手续费前） */
    @TableField("profit_amount")
    var profitAmount: BigDecimal? = null

    @TableField("profit_rate")
    var profitRate: BigDecimal? = null

    @TableField("fee_rate")
    var feeRate: BigDecimal? = null

    @TableField("fee_amount")
    var feeAmount: BigDecimal? = null

    @TableField("user_visible")
    var userVisible: Int? = null

    @TableField("remark")
    var remark: String? = null

    @TableField("create_admin_id")
    var createAdminId: Long? = null

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null
}
