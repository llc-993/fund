package com.fund.modules.wallet.model

import com.baomidou.mybatisplus.annotation.*
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 钱包操作日志表
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
@Schema(description = "钱包操作日志信息")
@TableName("app_wallet_operation_log")
class AppWalletOperationLog : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "操作日志主键ID", example = "1001", nullable = true)
    var id: Long? = null

    /**
     * 流水号
     */
    @TableField("serial_no")
    @Schema(description = "操作流水号", example = "OP20251027001", nullable = true)
    var serialNo: String? = null

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "关联用户ID", example = "10001", nullable = true)
    var userId: Long? = null

    /**
     * 钱包类型
     */
    @TableField("wallet_type")
    @Schema(description = "钱包类型（0=主钱包，1=交易钱包，2=冻结钱包）", example = "0", nullable = true)
    var walletType: Int? = null

    /**
     * 操作类型
     */
    @TableField("operation_type")
    @Schema(description = "操作类型（如充值、提现、交易等）", example = "DEPOSIT", nullable = true)
    var operationType: String? = null

    /**
     * 操作金额
     */
    @TableField("amount")
    @Schema(description = "操作金额", example = "100.00", nullable = true)
    var amount: BigDecimal? = null

    /**
     * 操作前余额
     */
    @TableField("before_balance")
    @Schema(description = "操作前余额", example = "500.00", nullable = true)
    var beforeBalance: BigDecimal? = null

    /**
     * 操作后余额
     */
    @TableField("after_balance")
    @Schema(description = "操作后余额", example = "600.00", nullable = true)
    var afterBalance: BigDecimal? = null

    /**
     * 关联业务ID
     */
    @TableField("related_id")
    @Schema(description = "关联业务ID（如订单ID）", example = "5001", nullable = true)
    var relatedId: Long? = null

    /**
     * 关联业务类型
     */
    @TableField("related_type")
    @Schema(description = "关联业务类型（如充值、提现）", example = "DEPOSIT", nullable = true)
    var relatedType: String? = null

    /**
     * 状态：0-失败，1-成功，2-处理中
     */
    @TableField("status")
    @Schema(description = "操作状态（0=失败，1=成功，2=处理中）", example = "1", nullable = true)
    var status: Int? = null

    /**
     * 备注
     */
    @TableField("remark")
    @Schema(description = "操作备注信息", example = "用户充值100USDT", nullable = true)
    var remark: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "操作记录时间", example = "2025-10-18T10:30:00", nullable = true)
    var createTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppWalletOperationLog{" +
                "id=" + id +
                ", serialNo='" + serialNo + '\'' +
                ", userId=" + userId +
                ", walletType=" + walletType +
                ", operationType='" + operationType + '\'' +
                ", amount=" + amount +
                ", beforeBalance=" + beforeBalance +
                ", afterBalance=" + afterBalance +
                ", relatedId=" + relatedId +
                ", relatedType='" + relatedType + '\'' +
                ", status=" + status +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                '}'
    }
}
