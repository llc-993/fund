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
 * AI 量化周期：预约→审核→建单→完成。
 * phase=2 时表示周期已完成且订单可对用户可见。
 */
@TableName("app_ai_quant_cycle")
class AppAiQuantCycle : Serializable {

    @TableId(type = IdType.AUTO)
    var id: Long? = null

    @TableField("user_id")
    var userId: Long? = null

    @TableField("wallet_id")
    var walletId: Long? = null

    @TableField("cycle_no")
    var cycleNo: String? = null

    /** 预约金额 */
    @TableField("request_amount")
    var requestAmount: BigDecimal? = null

    /** 核定本金 */
    @TableField("approved_amount")
    var approvedAmount: BigDecimal? = null

    /** 周期净盈利（毛利减手续费后的净值） */
    @TableField("profit_amount")
    var profitAmount: BigDecimal? = null

    @TableField("fee_amount")
    var feeAmount: BigDecimal? = null

    /** 0待审 1处理中 2已完成 -1驳回 */
    @TableField("phase")
    var phase: Int? = null

    @TableField("audit_admin_id")
    var auditAdminId: Long? = null

    @TableField("audit_time")
    var auditTime: LocalDateTime? = null

    @TableField("reject_reason")
    var rejectReason: String? = null

    @TableField("finish_time")
    var finishTime: LocalDateTime? = null

    @TableField("linked_order_id")
    var linkedOrderId: Long? = null

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null
}
