package com.fund.modules.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "用户登录请求")
class UserLoginRequest {

    @Schema(description = "登录账号（支持用户名或手机号）", required = true, example = "13800138000")
    @NotBlank(message = "username_not_empty")
    var userAccount: String? = null

    @Schema(description = "记住我，默认开启", example = "true")
    var rememberMe: Boolean = true

    @Schema(description = "登录密码", required = true, example = "123456")
    @NotBlank
    var password: String? = null
}