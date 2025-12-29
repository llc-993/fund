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
 * 支付平台配置
 * </p>
 *
 * @author 书记
 * @since 2025-12-27
 */
@TableName("app_pay_platform")
class AppPayPlatform : Serializable {

    /**
     * 支付渠道id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 支付渠道名称(前台显示名称)
     */
    @TableField("platform_name")
    var platformName: String? = null

    /**
     * 别名(前台不显示)
     */
    @TableField("alias")
    var alias: String? = null

    /**
     * 支付渠道编码
     */
    @TableField("code")
    var code: String? = null

    /**
     * 支付渠道图片
     */
    @TableField("image")
    var image: String? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

    /**
     * 状态 0-关闭 1-开启
     */
    @TableField("status")
    var status: Int? = null

    /**
     * 排序字段
     */
    @TableField("sort_by")
    var sortBy: Int? = null

    /**
     * 创建人
     */
    @TableField("create_user")
    var createUser: String? = null

    /**
     * 更新人
     */
    @TableField("update_user")
    var updateUser: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppPayPlatform{" +
        "id=" + id +
        ", platformName=" + platformName +
        ", alias=" + alias +
        ", code=" + code +
        ", image=" + image +
        ", remark=" + remark +
        ", status=" + status +
        ", sortBy=" + sortBy +
        ", createUser=" + createUser +
        ", updateUser=" + updateUser +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}"
    }
}
