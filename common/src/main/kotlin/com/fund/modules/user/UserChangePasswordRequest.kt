package com.fund.modules.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class UserChangePasswordRequest {

    /**
     * 旧密码(md5加密)
     */
    var oldPassword: String? = null

    //新密码(明文，请勿md5)
    @NotBlank(message = "money_password_not_empty")
    @Size(min = 4, max = 32, message = "password_length_limit")
    var newPassword: String? = null

    //确认密码(明文，请勿md5)
    @NotBlank(message = "money_password_not_empty")
    @Size(min = 4, max = 32, message = "password_length_limit")
    var confirmPassword: String? = null

}