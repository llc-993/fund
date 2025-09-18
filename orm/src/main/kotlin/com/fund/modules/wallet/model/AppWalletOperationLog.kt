package com.fund.modules.wallet.model

import com.baomidou.mybatisplus.annotation.*
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
@TableName("app_wallet_operation_log")
class AppWalletOperationLog : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 流水号
     */
    @TableField("serial_no")
    var serialNo: String? = null

    /**
     * 用户ID
     */
    @TableField("user_id")
    var userId: Long? = null

    /**
     * 钱包类型
     */
    @TableField("wallet_type")
    var walletType: Int? = null

    /**
     * 操作类型
     */
    @TableField("operation_type")
    var operationType: String? = null

    /**
     * 操作金额
     */
    @TableField("amount")
    var amount: BigDecimal? = null

    /**
     * 操作前余额
     */
    @TableField("before_balance")
    var beforeBalance: BigDecimal? = null

    /**
     * 操作后余额
     */
    @TableField("after_balance")
    var afterBalance: BigDecimal? = null

    /**
     * 关联业务ID
     */
    @TableField("related_id")
    var relatedId: Long? = null

    /**
     * 关联业务类型
     */
    @TableField("related_type")
    var relatedType: String? = null

    /**
     * 状态：0-失败，1-成功，2-处理中
     */
    @TableField("status")
    var status: Int? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
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
