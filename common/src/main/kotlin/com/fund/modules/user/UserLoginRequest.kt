package com.fund.modules.user

import jakarta.validation.constraints.NotBlank

class UserLoginRequest {


    // 登陆账号
    @NotBlank(message = "username_not_empty")
    var userAccount: String? = null

    // 记住我  默认开启
    var rememberMe: Boolean = true

    // 密码
    @NotBlank
    var password: String? = null
}