package com.fund.modules.quotation

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/**
 * 设置用户行情调控请求
 */
@Schema(description = "设置用户行情调控请求")
data class SetQuotationControlRequest(
    @Schema(description = "用户ID", required = true)
    val userId: Long,
    
    @Schema(description = "股票代码", required = true)
    val symbol: String,
    
    @Schema(description = "市场类型(US/CN/IN等)", required = true)
    val stockType: String,
    
    @Schema(description = "价格浮动值(正数修改high,负数修改low)", required = true)
    val floating: BigDecimal,

    @Schema(description = "调控生效时间戳(秒级)", required = true)
    val effectTime: Long,
    
    @Schema(description = "备注")
    val remark: String? = null
)

/**
 * 查询用户行情调控请求
 */
@Schema(description = "查询用户行情调控请求")
data class QueryQuotationControlRequest(
    @Schema(description = "用户ID")
    val userId: Long? = null,
    
    @Schema(description = "股票代码")
    val symbol: String? = null,
    
    @Schema(description = "市场类型")
    val stockType: String? = null,
    
    @Schema(description = "是否启用")
    val isActive: Byte? = null
)

/**
 * 删除用户行情调控请求
 */
@Schema(description = "删除用户行情调控请求")
data class DeleteQuotationControlRequest(
    @Schema(description = "调控ID", required = true)
    val id: Long
)
