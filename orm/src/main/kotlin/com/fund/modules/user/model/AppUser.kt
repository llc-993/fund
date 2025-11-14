package com.fund.modules.user.model;

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fund.modules.wallet.model.AppUserWalletV2
import io.swagger.v3.oas.annotations.media.Schema
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
@Schema(description = "平台用户信息")
@TableName("app_user")
class AppUser : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户主键ID", example = "10001", nullable = true)
    var id: Long? = null

    /**
     * 总代用户ID
     */
    @TableField("top_user_id")
    @Schema(description = "上级代理用户ID", example = "20001", nullable = true)
    var topUserId: Long? = null

    /**
     * 用户昵称
     */
    @TableField("user_name")
    @Schema(description = "用户昵称", example = "小明", nullable = true)
    var userName: String? = null

    /**
     * 用户登录账号
     */
    @TableField("user_account")
    @Schema(description = "用户登录账号", example = "xiaoming88", nullable = true)
    var userAccount: String? = null

    @TableField("keyword")
    @Schema(description = "搜索关键词或别名", example = "XM88", nullable = true)
    var keyword: String? = null

    /**
     * 邀请码
     */
    @TableField("share_code")
    @Schema(description = "用户邀请码", example = "INV123456", nullable = true)
    var shareCode: String? = null

    /**
     * 注册的手机号
     */
    @TableField("mobile_phone")
    @Schema(description = "绑定手机号", example = "+8613712345678", nullable = true)
    var mobilePhone: String? = null

    /**
     * 登录密码
     */
    @JsonIgnore
    @TableField("password")
    @Schema(description = "登录密码（加密存储）", example = "******", nullable = true)
    var password: String? = null

    /**
     * 登录密码(明码)
     */
    @JsonIgnore
    @TableField("show_password")
    @Schema(description = "登录密码明文（敏感字段，仅测试使用）", example = "123456", nullable = true)
    var showPassword: String? = null

    /**
     * 交易密码
     */
    @JsonIgnore
    @TableField("money_password")
    @Schema(description = "资金密码（加密存储）", example = "******", nullable = true)
    var moneyPassword: String? = null

    /**
     * 交易密码(明码)
     */
    @JsonIgnore
    @TableField("show_money_password")
    @Schema(description = "资金密码明文", example = "654321", nullable = true)
    var showMoneyPassword: String? = null

    /**
     * 资源服务器域名
     */
    @TableField("source_host")
    @Schema(description = "资源服务器域名", example = "https://cdn.example.com", nullable = true)
    var sourceHost: String? = null

    /**
     * 用户头像
     */
    @TableField("avatar")
    @Schema(description = "用户头像地址", example = "https://cdn.example.com/avatar.png", nullable = true)
    var avatar: String? = null

    /**
     * 正常 0 假人 1
     */
    @TableField("user_group")
    @Schema(description = "用户组类型（0=正常用户，1=虚拟用户）", example = "0", nullable = true)
    var userGroup: Int? = null

    /**
     * 性别 1 : 男 ， 0 : 女 -1：未知
     */
    @TableField("gender")
    @Schema(description = "用户性别（1=男，0=女，-1=未知）", example = "1", nullable = true)
    var gender: Int? = null

    /**
     * 历史重置次数
     */
    @JsonIgnore
    @TableField("task_reset_count")
    @Schema(description = "任务重置次数", example = "2", nullable = true)
    var taskResetCount: Int? = null

    /**
     * 是否冻结 0否1是
     */
    @TableField("is_frozen")
    @Schema(description = "是否冻结（true=已冻结）", example = "false", nullable = true)
    var isFrozen: Boolean? = null

    /**
     * 是否允许交易 0否1是
     */
    @TableField("tradable")
    @Schema(description = "是否允许交易（true=允许）", example = "true", nullable = true)
    var tradable: Boolean? = null

    /**
     * 是否允许提现 0否1是
     */
    @TableField("cashable")
    @Schema(description = "是否允许提现（true=允许）", example = "true", nullable = true)
    var cashable: Boolean? = null

    /**
     * 等级名称
     */
    @TableField("level_name")
    @Schema(description = "会员等级名称", example = "VIP1", nullable = true)
    var levelName: String? = null

    /**
     * 等级权重，高向低兼容
     */
    @TableField("level_weights")
    @Schema(description = "会员等级权重（数值越大等级越高）", example = "10", nullable = true)
    var levelWeights: Int? = null

    /**
     * 注册ip
     */
    @TableField("register_ip")
    @Schema(description = "注册IP地址", example = "192.168.1.10", nullable = true)
    var registerIp: String? = null

    /**
     * 注册地区
     */
    @TableField("register_area")
    @Schema(description = "注册地区", example = "CN-Guangdong", nullable = true)
    var registerArea: String? = null

    /**
     * 注册时间
     */
    @TableField("register_time")
    @Schema(description = "注册时间", example = "2025-08-21T15:30:00", nullable = true)
    var registerTime: LocalDateTime? = null

    /**
     * 最后登录ip
     */
    @TableField("last_login_ip")
    @Schema(description = "最近登录IP地址", example = "10.0.0.2", nullable = true)
    var lastLoginIp: String? = null

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    @Schema(description = "最近登录时间", example = "2025-10-18T09:15:00", nullable = true)
    var lastLoginTime: LocalDateTime? = null

    @TableField("kyc_status")
    @Schema(description = "kyc状态（0:未提交，1:已经提交、待审核，2:审核通过，3:审核失败）")
    var kycStatus: Int? = null

    /**
     * 身份信息正面
     */
    @TableField("kyc_pic1")
    @Schema(description = "身份信息正面")
    var kycPic1: String? = null

    /**
     * 身份信息反面
     */
    @TableField("kyc_pic2")
    @Schema(description = "身份信息反面")
    var kycPic2: String? = null

    /**
     * 身份信息反面
     */
    @TableField("id_number")
    @Schema(description = "身份ID号")
    var idNumber: String? = null

    @TableField(exist = false)
    @Schema(description = "关联的钱包信息列表", nullable = true)
    var wallet: List<AppUserWalletV2>? = null

    override fun toString(): String {
        return "AppUser(idNumber=$idNumber, id=$id, topUserId=$topUserId, userName=$userName, userAccount=$userAccount, keyword=$keyword, shareCode=$shareCode, mobilePhone=$mobilePhone, password=$password, showPassword=$showPassword, moneyPassword=$moneyPassword, showMoneyPassword=$showMoneyPassword, sourceHost=$sourceHost, avatar=$avatar, userGroup=$userGroup, gender=$gender, taskResetCount=$taskResetCount, isFrozen=$isFrozen, tradable=$tradable, cashable=$cashable, levelName=$levelName, levelWeights=$levelWeights, registerIp=$registerIp, registerArea=$registerArea, registerTime=$registerTime, lastLoginIp=$lastLoginIp, lastLoginTime=$lastLoginTime, kycStatus=$kycStatus, kycPic1=$kycPic1, kycPic2=$kycPic2, wallet=$wallet)"
    }
}
