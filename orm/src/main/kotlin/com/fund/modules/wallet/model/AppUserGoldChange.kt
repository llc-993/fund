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
 * 会员账变记录表
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
@TableName("app_user_gold_change")
class AppUserGoldChange : Serializable {

    /**
     * 账表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 账变流水号
     */
    @TableField("serial_no")
    var serialNo: String? = null

    /**
     * 用户id
     */
    @TableField("user_id")
    var userId: Long? = null

    /**
     * 总代用户ID
     */
    @TableField("top_user_id")
    var topUserId: Long? = null

    /**
     * 资产类型 0：余额 1：冻结金额
     */
    @TableField("asset_type")
    var assetType: Int? = null

    /**
     * 会员账号
     */
    @TableField("user_account")
    var userAccount: String? = null

    /**
     * 变动类型
     */
    @TableField("change_type")
    var changeType: Int? = null

    /**
     * 变动前金额
     */
    @TableField("before_amount")
    var beforeAmount: BigDecimal? = null

    /**
     * 变动后金额
     */
    @TableField("after_amount")
    var afterAmount: BigDecimal? = null

    /**
     * 账变金额
     */
    @TableField("amount")
    var amount: BigDecimal? = null

    /**
     * 操作说明
     */
    @TableField("op_note")
    var opNote: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 正常0 假人1
     */
    @TableField("user_group")
    var userGroup: Int? = null

    override fun toString(): String {
        return "AppUserGoldChange{" +
        "id=" + id +
        ", serialNo=" + serialNo +
        ", userId=" + userId +
        ", topUserId=" + topUserId +
        ", assetType=" + assetType +
        ", userAccount=" + userAccount +
        ", changeType=" + changeType +
        ", beforeAmount=" + beforeAmount +
        ", afterAmount=" + afterAmount +
        ", amount=" + amount +
        ", opNote=" + opNote +
        ", createTime=" + createTime +
        ", userGroup=" + userGroup +
        "}"
    }
}
