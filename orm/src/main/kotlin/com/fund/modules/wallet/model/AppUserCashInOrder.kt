package com.fund.modules.wallet.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 用户充值订单表
 * </p>
 *
 * @author 书记
 * @since 2025-10-18
 */
@Schema(description = "用户充值订单信息")
@TableName("app_user_cash_in_order")
class AppUserCashInOrder : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "充值订单ID", example = "1001", nullable = true)
    var id: Long? = null

    /**
     * 用户id
     */
    @TableField("user_id")
    @Schema(description = "用户ID", example = "10001", nullable = true)
    var userId: Long? = null

    /**
     * 正常 0 假人 1
     */
    @TableField("user_group")
    @Schema(description = "用户组类型（0=正常用户，1=虚拟用户）", example = "0", nullable = true)
    var userGroup: Int? = null

    /**
     * 用户名
     */
    @TableField("user_account")
    @Schema(description = "用户登录账号", example = "xiaoming88", nullable = true)
    var userAccount: String? = null

    /**
     * 手机号
     */
    @TableField("mobile_phone")
    @Schema(description = "用户手机号", example = "+8613712345678", nullable = true)
    var mobilePhone: String? = null

    /**
     * 总代用户ID
     */
    @TableField("top_user_id")
    @Schema(description = "上级代理用户ID", example = "20001", nullable = true)
    var topUserId: Long? = null

    /**
     * ip
     */
    @TableField("ip")
    @Schema(description = "充值操作IP地址", example = "192.168.1.100", nullable = true)
    var ip: String? = null

    /**
     * 订单编号
     */
    @TableField("order_no")
    @Schema(description = "充值订单编号", example = "CZ20251018001", nullable = true)
    var orderNo: String? = null

    /**
     * 申请时间
     */
    @TableField("apply_time")
    @Schema(description = "充值申请时间", example = "2025-10-18T09:30:00", nullable = true)
    var applyTime: LocalDateTime? = null

    /**
     * 申请提现金额
     */
    @TableField("apply_amount")
    @Schema(description = "充值申请金额", example = "1000.00", nullable = true)
    var applyAmount: BigDecimal? = null

    /**
     * 用户备注
     */
    @TableField("remark")
    @Schema(description = "用户备注信息", example = "微信转账", nullable = true)
    var remark: String? = null

    /**
     * 审核时间
     */
    @TableField("remit_time")
    @Schema(description = "充值审核时间", example = "2025-10-18T10:15:00", nullable = true)
    var remitTime: LocalDateTime? = null

    /**
     * 订单类型   1待处理 2已锁定 3已取消 4已拒绝 5已成功
     */
    @TableField("cash_status")
    @Schema(description = "充值订单状态（1=待处理，2=已锁定，3=已取消，4=已拒绝，5=已成功）", example = "1", nullable = true)
    var cashStatus: Int? = null

    /**
     * 失败原因,如果有
     */
    @TableField("reason")
    @Schema(description = "充值失败原因（如有）", example = "支付凭证无效", nullable = true)
    var reason: String? = null

    /**
     * 订单处理--操作人账号
     */
    @TableField("operator_user")
    @Schema(description = "处理订单的操作人账号", example = "admin", nullable = true)
    var operatorUser: String? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "订单更新时间", example = "2025-10-18T10:30:00", nullable = true)
    var updateTime: LocalDateTime? = null

    @TableField("img_url")
    @Schema(description = "充值凭证图片URL", example = "https://cdn.example.com/images/receipt.jpg", nullable = true)
    var imgUrl: String? = null

    override fun toString(): String {
        return "AppUserCashInOrder{" +
        "id=" + id +
        ", userId=" + userId +
        ", userGroup=" + userGroup +
        ", userAccount=" + userAccount +
        ", mobilePhone=" + mobilePhone +
        ", topUserId=" + topUserId +
        ", ip=" + ip +
        ", orderNo=" + orderNo +
        ", applyTime=" + applyTime +
        ", applyAmount=" + applyAmount +
        ", remark=" + remark +
        ", remitTime=" + remitTime +
        ", cashStatus=" + cashStatus +
        ", reason=" + reason +
        ", operatorUser=" + operatorUser +
        ", updateTime=" + updateTime +
        "}"
    }
}
