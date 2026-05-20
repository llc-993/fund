package com.fund.modules.gold.model

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/** 用户在某渠道的积存金持仓 */
@TableName("app_gold_position")
class AppGoldPosition : Serializable {

    @TableId(type = IdType.AUTO)
    var id: Long? = null

    @TableField("user_id")
    var userId: Long? = null

    @TableField("gold_wallet_id")
    var goldWalletId: Long? = null

    @TableField("cash_wallet_id")
    var cashWalletId: Long? = null

    @TableField("channel_id")
    var channelId: Long? = null

    @TableField("channel_code")
    var channelCode: String? = null

    @TableField("currency_code")
    var currencyCode: String? = null

    @TableField("channel_name_snapshot")
    var channelNameSnapshot: String? = null

    @TableField("account_label_snapshot")
    var accountLabelSnapshot: String? = null

    @TableField("hold_grams")
    var holdGrams: BigDecimal? = null

    @TableField("hold_cost")
    var holdCost: BigDecimal? = null

    @TableField("cost_avg_price")
    var costAvgPrice: BigDecimal? = null

    @TableField("last_market_value")
    var lastMarketValue: BigDecimal? = null

    @TableField("last_holding_profit")
    var lastHoldingProfit: BigDecimal? = null

    @TableField("cumulative_profit")
    var cumulativeProfit: BigDecimal? = null

    @TableField("cumulative_invest")
    var cumulativeInvest: BigDecimal? = null

    @TableField("cumulative_buy_fee")
    var cumulativeBuyFee: BigDecimal? = null

    @TableField("cumulative_sell_fee")
    var cumulativeSellFee: BigDecimal? = null

    @TableField("today_profit")
    var todayProfit: BigDecimal? = null

    @TableField("today_profit_date")
    var todayProfitDate: LocalDate? = null

    @TableField("last_buy_time")
    var lastBuyTime: LocalDateTime? = null

    @TableField("last_sell_time")
    var lastSellTime: LocalDateTime? = null

    @TableField("last_evaluate_time")
    var lastEvaluateTime: LocalDateTime? = null

    @TableField("status")
    var status: Int? = null

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null
}
