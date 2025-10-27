package com.fund.modules.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "修改密码请求")
class UserChangePasswordRequest {

    @Schema(description = "旧密码（MD5加密）", required = true, example = "e10adc3949ba59abbe56e057f20f883e")
    var oldPassword: String? = null

    @Schema(description = "新密码（明文，4-32位字符）", required = true, example = "newpassword123")
    @NotBlank(message = "money_password_not_empty")
    @Size(min = 4, max = 32, message = "password_length_limit")
    var newPassword: String? = null

    @Schema(description = "确认新密码（明文，4-32位字符）", required = true, example = "newpassword123")
    @NotBlank(message = "money_password_not_empty")
    @Size(min = 4, max = 32, message = "password_length_limit")
    var confirmPassword: String? = null

}