package com.fund.modules.gold.model

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/** 积存金渠道（银行积存金账户维度） */
@TableName("app_gold_channel")
class AppGoldChannel : Serializable {

    @TableId(type = IdType.AUTO)
    var id: Long? = null

    @TableField("channel_code")
    var channelCode: String? = null

    @TableField("channel_name")
    var channelName: String? = null

    @TableField("bank_name")
    var bankName: String? = null

    @TableField("account_label")
    var accountLabel: String? = null

    @TableField("account_tag")
    var accountTag: String? = null

    @TableField("logo_url")
    var logoUrl: String? = null

    @TableField("cs_link")
    var csLink: String? = null

    @TableField("risk_notice_url")
    var riskNoticeUrl: String? = null

    @TableField("currency_code")
    var currencyCode: String? = null

    @TableField("buy_fee_rate")
    var buyFeeRate: BigDecimal? = null

    @TableField("sell_fee_rate")
    var sellFeeRate: BigDecimal? = null

    @TableField("min_buy_amount")
    var minBuyAmount: BigDecimal? = null

    @TableField("min_sell_grams")
    var minSellGrams: BigDecimal? = null

    @TableField("gram_scale")
    var gramScale: Int? = null

    @TableField("price_tolerance_bps")
    var priceToleranceBps: Int? = null

    @TableField("sort_order")
    var sortOrder: Int? = null

    @TableField("enable_flag")
    var enableFlag: Int? = null

    @TableField("remark")
    var remark: String? = null

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null
}
