package com.fund.modules.wallet.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 用户提现订单表
 * </p>
 *
 * @author 书记
 * @since 2025-10-18
 */
@TableName("app_user_cash_out_order")
class AppUserCashOutOrder : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 用户id
     */
    @TableField("user_id")
    var userId: Long? = null

    /**
     * 正常 0 假人 1
     */
    @TableField("user_group")
    var userGroup: Int? = null

    /**
     * 用户名
     */
    @TableField("user_account")
    var userAccount: String? = null

    /**
     * 总代用户ID
     */
    @TableField("top_user_id")
    var topUserId: Long? = null

    /**
     * ip
     */
    @TableField("ip")
    var ip: String? = null

    /**
     * 订单编号
     */
    @TableField("order_no")
    var orderNo: String? = null

    /**
     * 申请时间
     */
    @TableField("apply_time")
    var applyTime: LocalDateTime? = null

    /**
     * 申请提现金额
     */
    @TableField("apply_amount")
    var applyAmount: BigDecimal? = null

    /**
     * 实际打款金额
     */
    @TableField("actual_amount")
    var actualAmount: BigDecimal? = null

    /**
     * 手续费
     */
    @TableField("fee")
    var fee: BigDecimal? = null

    /**
     * 真实姓名
     */
    @TableField("full_name")
    var fullName: String? = null

    /**
     * 提现网络 Tron Eth
     */
    @TableField("net_work")
    var netWork: String? = null

    /**
     * 钱包地址
     */
    @TableField("address")
    var address: String? = null

    /**
     * 提现手机号
     */
    @TableField("mobile_phone")
    var mobilePhone: String? = null

    /**
     * 用户备注
     */
    @TableField("remark")
    var remark: String? = null

    /**
     * 审核时间
     */
    @TableField("remit_time")
    var remitTime: LocalDateTime? = null

    /**
     * 订单类型 1待处理 2已锁定 3已取消 4已拒绝 5已成功
     */
    @TableField("cash_status")
    var cashStatus: Int? = null

    /**
     * 失败原因,如果有
     */
    @TableField("reason")
    var reason: String? = null

    /**
     * hash,如果有
     */
    @TableField("hash")
    var hash: String? = null

    /**
     * 订单处理--操作人账号
     */
    @TableField("operator_user")
    var operatorUser: String? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppUserCashOutOrder{" +
        "id=" + id +
        ", userId=" + userId +
        ", userGroup=" + userGroup +
        ", userAccount=" + userAccount +
        ", topUserId=" + topUserId +
        ", ip=" + ip +
        ", orderNo=" + orderNo +
        ", applyTime=" + applyTime +
        ", applyAmount=" + applyAmount +
        ", actualAmount=" + actualAmount +
        ", fee=" + fee +
        ", fullName=" + fullName +
        ", netWork=" + netWork +
        ", address=" + address +
        ", mobilePhone=" + mobilePhone +
        ", remark=" + remark +
        ", remitTime=" + remitTime +
        ", cashStatus=" + cashStatus +
        ", reason=" + reason +
        ", hash=" + hash +
        ", operatorUser=" + operatorUser +
        ", updateTime=" + updateTime +
        "}"
    }
}
