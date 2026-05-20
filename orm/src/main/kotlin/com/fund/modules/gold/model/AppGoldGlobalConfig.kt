package com.fund.modules.gold.model

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/** 积存金全局配置（单行，主键固定为 1） */
@TableName("app_gold_global_config")
class AppGoldGlobalConfig : Serializable {

    @TableId(value = "id", type = IdType.INPUT)
    var id: Int? = 1

    @TableField("default_buy_fee_rate")
    var defaultBuyFeeRate: BigDecimal? = null

    @TableField("default_sell_fee_rate")
    var defaultSellFeeRate: BigDecimal? = null

    @TableField("default_min_buy_amount")
    var defaultMinBuyAmount: BigDecimal? = null

    @TableField("default_min_sell_grams")
    var defaultMinSellGrams: BigDecimal? = null

    @TableField("default_gram_scale")
    var defaultGramScale: Int? = null

    @TableField("default_price_tolerance_bps")
    var defaultPriceToleranceBps: Int? = null

    @TableField("currency_code")
    var currencyCode: String? = null

    @TableField("quote_cache_seconds")
    var quoteCacheSeconds: Int? = null

    @TableField("risk_notice_url")
    var riskNoticeUrl: String? = null

    @TableField("entry_enable")
    var entryEnable: Int? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null
}
