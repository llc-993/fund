package com.fund.modules.gold.model

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/** 渠道实时金价（每渠道一行） */
@TableName("app_gold_price_quote")
class AppGoldPriceQuote : Serializable {

    @TableId(type = IdType.AUTO)
    var id: Long? = null

    @TableField("channel_id")
    var channelId: Long? = null

    @TableField("channel_code")
    var channelCode: String? = null

    @TableField("price")
    var price: BigDecimal? = null

    @TableField("prev_close_price")
    var prevClosePrice: BigDecimal? = null

    @TableField("change_amount")
    var changeAmount: BigDecimal? = null

    @TableField("change_pct")
    var changePct: BigDecimal? = null

    @TableField("intraday_high")
    var intradayHigh: BigDecimal? = null

    @TableField("intraday_low")
    var intradayLow: BigDecimal? = null

    @TableField("intraday_open")
    var intradayOpen: BigDecimal? = null

    @TableField("quote_time")
    var quoteTime: LocalDateTime? = null

    @TableField("trading_status")
    var tradingStatus: Int? = null

    @TableField("volume")
    var volume: BigDecimal? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null
}
