package com.fund.modules.auth

import jakarta.validation.constraints.NotBlank


class AdminAuthRequest {
    /**
     * 登陆账号
     */
    @NotBlank
    var account: String? = null

    /**
     * 验证码
     */
    var code: String? = null

    @NotBlank
    var password: String? = null
}