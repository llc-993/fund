package com.fund.modules.sys.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@TableName("sys_user")
class SysUser : Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    var id: Int? = null

    /**
     * 用户名
     */
    @TableField("username")
    var username: String? = null

    /**
     * 昵称
     */
    @TableField("nickname")
    var nickname: String? = null

    /**
     * 用户组 1正式组 0测试组
     */
    @TableField("user_group")
    var userGroup: Int? = null

    /**
     * 谷歌密钥
     */
    @TableField("ga_key")
    var gaKey: String? = null

    /**
     * 开启安全模式，开启后谷歌密钥才有效
     */
    @TableField("enable_safe_mode")
    var enableSafeMode: Boolean? = null

    /**
     * 密码
     */
    @TableField("password")
    var password: String? = null

    /**
     * 部门ID
     */
    @TableField("dept_id")
    var deptId: Int? = null

    /**
     * 用户头像
     */
    @TableField("avatar")
    var avatar: String? = null

    /**
     * 用户状态((1:正常;0:禁用))
     */
    @TableField("status")
    var status: Boolean? = null

    /**
     * 用户邮箱
     */
    @TableField("email")
    var email: String? = null

    /**
     * 逻辑删除标识(0:未删除;1:已删除)
     */
    @TableField("deleted")
    var deleted: Boolean? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    /**
     * 角色
     */
    @TableField("role_ids")
    var roleIds: String? = null

    override fun toString(): String {
        return "SysUser{" +
        "id=" + id +
        ", username=" + username +
        ", nickname=" + nickname +
        ", userGroup=" + userGroup +
        ", gaKey=" + gaKey +
        ", enableSafeMode=" + enableSafeMode +
        ", password=" + password +
        ", deptId=" + deptId +
        ", avatar=" + avatar +
        ", status=" + status +
        ", email=" + email +
        ", deleted=" + deleted +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", roleIds=" + roleIds +
        "}"
    }
}
