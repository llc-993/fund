package com.fund.modules.user

import jakarta.validation.constraints.NotBlank

class UserRegisterRequest {

    // 用户昵称
    @NotBlank(message = "username_not_empty")
    var username: String? = null

    @NotBlank(message = "password_not_empty")
    var password: String? = null

    @NotBlank(message = "confirm_password")
    var confirmPassword: String? = null

    // 手机号
    var mobilePhone: String? = null

    // 邀请码
    var shareCode: String? = null

    // 提现密码
    var moneyPassword: String? = null
}