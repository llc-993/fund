package com.fund.modules.platform.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 支付渠道用户绑定
 * </p>
 *
 * @author 书记
 * @since 2025-12-27
 */
@TableName("app_pay_platform_user")
class AppPayPlatformUser : Serializable {

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
     * 支付渠道编码
     */
    @TableField("platform_code")
    var platformCode: String? = null

    /**
     * 渠道地址
     */
    @TableField("address")
    var address: String? = null

    /**
     * 是否删除
     */
    @TableField("deleted")
    var deleted: Boolean? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(exist = false)
    var platformName: String? = null


    override fun toString(): String {
        return "AppPayPlatformUser{" +
        "id=" + id +
        ", userId=" + userId +
        ", userGroup=" + userGroup +
        ", userAccount=" + userAccount +
        ", topUserId=" + topUserId +
        ", platformCode=" + platformCode +
        ", address=" + address +
        ", deleted=" + deleted +
        ", createTime=" + createTime +
        "}"
    }
}
