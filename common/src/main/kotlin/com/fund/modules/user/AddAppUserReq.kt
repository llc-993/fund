package com.fund.modules.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.io.Serializable

@Schema(description ="管理后台新增会员")
class AddAppUserReq : Serializable{

    @Schema(description = "用户登录账号", required = true)
    var userAccount: @NotBlank String? = null

    @Schema(description = "邀请码", required = true)
    var shareCode: @NotBlank String? = null

    @Schema(description = "手机号", required = true)
    var mobilePhone: @NotBlank String? = null

    @Schema(description = "登录密码", required = true)
    var password: @NotBlank String? = null

    @Schema(description = "交易密码", required = true)
    var moneyPassword: @NotBlank String? = null

    @Schema(description = "会员等级", required = true)
    var levelWeights: @NotNull Int? = null

    @Schema(description = "是否假人 正常 0 假人 1", required = true)
    var userGroup: @NotNull Int? = null

    @Schema(description = "状态 是否冻结", required = true)
    var isFrozen: @NotNull Boolean? = null

    @Schema(description = "是否允许交易", required = true)
    var tradable: @NotNull Boolean? = null

    @Schema(description = "是否允许提现", required = true)
    var cashable: @NotNull Boolean? = null

    @Schema(description ="年龄")
    var age: Int? = null

    @Schema(description ="个人信用分")
    var selfScore: Int? = null

    @Schema(description ="邮箱")
    var mail: String? = null
}