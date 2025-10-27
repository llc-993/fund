package com.fund.modules.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "用户注册请求")
class UserRegisterRequest {

    @Schema(description = "用户昵称", required = true, example = "张三")
    @NotBlank(message = "username_not_empty")
    var username: String? = null

    @Schema(description = "登录密码", required = true, example = "123456")
    @NotBlank(message = "password_not_empty")
    var password: String? = null

    @Schema(description = "确认密码", required = true, example = "123456")
    @NotBlank(message = "confirm_password")
    var confirmPassword: String? = null

    @Schema(description = "手机号", example = "13800138000")
    var mobilePhone: String? = null

    @Schema(description = "邀请码", example = "ABC123")
    var shareCode: String? = null

    @Schema(description = "提现密码", example = "123456")
    var moneyPassword: String? = null
}