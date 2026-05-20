package com.fund.modules.aiquant.model

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.time.LocalDateTime

/**
 * AI 量化客服渠道配置
 */
@TableName("app_ai_quant_service_channel")
class AppAiQuantServiceChannel : Serializable {

    @TableId(type = IdType.AUTO)
    var id: Long? = null

    @TableField("name")
    var name: String? = null

    @TableField("stock_id")
    var stockId: Long? = null

    @TableField("symbol")
    var symbol: String? = null

    @TableField("market")
    var market: String? = null

    @TableField("cs_link")
    var csLink: String? = null

    @TableField("sort_order")
    var sortOrder: Int? = null

    @TableField("enable")
    var enable: Int? = null

    @TableField("remark")
    var remark: String? = null

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null
}
