package com.fund.modules.user.model;

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@TableName("app_user")
class AppUser : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 总代用户ID
     */
    @TableField("top_user_id")
    var topUserId: Long? = null

    /**
     * 用户昵称
     */
    @TableField("user_name")
    var userName: String? = null

    /**
     * 用户登录账号
     */
    @TableField("user_account")
    var userAccount: String? = null

    @TableField("keyword")
    var keyword: String? = null

    /**
     * 邀请码
     */
    @TableField("share_code")
    var shareCode: String? = null

    /**
     * 注册的手机号
     */
    @TableField("mobile_phone")
    var mobilePhone: String? = null

    /**
     * 登录密码
     */
    @TableField("password")
    var password: String? = null

    /**
     * 登录密码(明码)
     */
    @TableField("show_password")
    var showPassword: String? = null

    /**
     * 交易密码
     */
    @TableField("money_password")
    var moneyPassword: String? = null

    /**
     * 交易密码(明码)
     */
    @TableField("show_money_password")
    var showMoneyPassword: String? = null

    /**
     * 资源服务器域名
     */
    @TableField("source_host")
    var sourceHost: String? = null

    /**
     * 用户头像
     */
    @TableField("avatar")
    var avatar: String? = null

    /**
     * 正常 0 假人 1
     */
    @TableField("user_group")
    var userGroup: Int? = null

    /**
     * 性别 1 : 男 ， 0 : 女 -1：未知
     */
    @TableField("gender")
    var gender: Int? = null

    /**
     * 历史重置次数
     */
    @TableField("task_reset_count")
    var taskResetCount: Int? = null

    /**
     * 是否冻结 0否1是
     */
    @TableField("is_frozen")
    var isFrozen: Boolean? = null

    /**
     * 是否允许交易 0否1是
     */
    @TableField("tradable")
    var tradable: Boolean? = null

    /**
     * 是否允许提现 0否1是
     */
    @TableField("cashable")
    var cashable: Boolean? = null

    /**
     * 等级名称
     */
    @TableField("level_name")
    var levelName: String? = null

    /**
     * 等级权重，高向低兼容
     */
    @TableField("level_weights")
    var levelWeights: Int? = null

    /**
     * 注册ip
     */
    @TableField("register_ip")
    var registerIp: String? = null

    /**
     * 注册地区
     */
    @TableField("register_area")
    var registerArea: String? = null

    /**
     * 注册时间
     */
    @TableField("register_time")
    var registerTime: LocalDateTime? = null

    /**
     * 最后登录ip
     */
    @TableField("last_login_ip")
    var lastLoginIp: String? = null

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    var lastLoginTime: LocalDateTime? = null


    override fun toString(): String {
        return "AppUser(id=$id, topUserId=$topUserId, userName=$userName, userAccount=$userAccount, keyword=$keyword, shareCode=$shareCode, mobilePhone=$mobilePhone, password=$password, showPassword=$showPassword, moneyPassword=$moneyPassword, showMoneyPassword=$showMoneyPassword, sourceHost=$sourceHost, avatar=$avatar, userGroup=$userGroup, gender=$gender, taskResetCount=$taskResetCount, isFrozen=$isFrozen, tradable=$tradable, cashable=$cashable, levelName=$levelName, levelWeights=$levelWeights, registerIp=$registerIp, registerArea=$registerArea, registerTime=$registerTime, lastLoginIp=$lastLoginIp, lastLoginTime=$lastLoginTime)"
    }

}
