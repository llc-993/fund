package com.fund.modules.gold.model

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 用户积存金汇总账户：按用户×币种一行，与现金钱包 [com.fund.modules.wallet.model.AppUserWalletV2] 分离，仅统计不直接动账。
 */
@TableName("app_user_gold_wallet")
class AppUserGoldWallet : Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    @TableField("user_id")
    var userId: Long? = null

    @TableField("top_user_id")
    var topUserId: Long? = null

    /** 关联 HKD 等现金钱包主键 */
    @TableField("wallet_id")
    var walletId: Long? = null

    @TableField("currency_code")
    var currencyCode: String? = null

    @TableField("total_grams")
    var totalGrams: BigDecimal? = null

    @TableField("total_cost")
    var totalCost: BigDecimal? = null

    @TableField("avg_cost_price")
    var avgCostPrice: BigDecimal? = null

    @TableField("total_invest")
    var totalInvest: BigDecimal? = null

    @TableField("total_realized_profit")
    var totalRealizedProfit: BigDecimal? = null

    @TableField("total_holding_profit")
    var totalHoldingProfit: BigDecimal? = null

    @TableField("total_market_value")
    var totalMarketValue: BigDecimal? = null

    @TableField("total_buy_fee")
    var totalBuyFee: BigDecimal? = null

    @TableField("total_sell_fee")
    var totalSellFee: BigDecimal? = null

    @TableField("last_evaluate_time")
    var lastEvaluateTime: LocalDateTime? = null

    /** 递增版本号（写路径由分布式交易锁串行化） */
    @JsonIgnore
    @TableField("version", update = "%s+1")
    var version: Int? = null

    @TableField("status")
    var status: Int? = null

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null
}
