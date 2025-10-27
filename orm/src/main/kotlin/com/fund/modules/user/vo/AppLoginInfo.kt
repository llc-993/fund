package com.fund.modules.user.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "登录信息")
class AppLoginInfo {

    @Schema(description = "登录Token", example = "bearer_token_123456")
    var token: String? = null

    @Schema(description = "用户账号", example = "13800138000")
    var account: String? = null

    @Schema(description = "用户头像URL", example = "https://example.com/avatar.jpg")
    var avatar: String? = null
}