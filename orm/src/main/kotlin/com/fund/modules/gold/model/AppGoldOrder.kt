package com.fund.modules.gold.model

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/** 积存金买卖订单 */
@TableName("app_gold_order")
class AppGoldOrder : Serializable {

    @TableId(type = IdType.AUTO)
    var id: Long? = null

    @TableField("order_no")
    var orderNo: String? = null

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

    @TableField("direction")
    var direction: Int? = null

    @TableField("price")
    var price: BigDecimal? = null

    @TableField("expect_price")
    var expectPrice: BigDecimal? = null

    @TableField("grams")
    var grams: BigDecimal? = null

    @TableField("amount")
    var amount: BigDecimal? = null

    @TableField("fee_rate")
    var feeRate: BigDecimal? = null

    @TableField("fee_amount")
    var feeAmount: BigDecimal? = null

    @TableField("wallet_change_amount")
    var walletChangeAmount: BigDecimal? = null

    @TableField("cost_avg_price_before")
    var costAvgPriceBefore: BigDecimal? = null

    @TableField("cost_avg_price_after")
    var costAvgPriceAfter: BigDecimal? = null

    @TableField("realized_profit")
    var realizedProfit: BigDecimal? = null

    @TableField("realized_profit_net")
    var realizedProfitNet: BigDecimal? = null

    @TableField("quote_id")
    var quoteId: Long? = null

    @TableField("status")
    var status: Int? = null

    @TableField("fail_reason")
    var failReason: String? = null

    @TableField("remark")
    var remark: String? = null

    @TableField("finish_time")
    var finishTime: LocalDateTime? = null

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null
}
