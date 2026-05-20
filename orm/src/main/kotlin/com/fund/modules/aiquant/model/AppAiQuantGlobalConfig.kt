package com.fund.modules.aiquant.model

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * AI 量化全局配置（单行 id=1）
 */
@TableName("app_ai_quant_global_config")
class AppAiQuantGlobalConfig : Serializable {

    @TableId("id")
    var id: Int? = null

    @TableField("min_reserve_amount")
    var minReserveAmount: BigDecimal? = null

    @TableField("fee_rate")
    var feeRate: BigDecimal? = null

    @TableField("replace_contract_entry")
    var replaceContractEntry: Int? = null

    @TableField("update_time")
    var updateTime: LocalDateTime? = null
}
