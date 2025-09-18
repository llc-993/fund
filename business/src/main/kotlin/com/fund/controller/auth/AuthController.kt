package com.fund.controller.auth

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.stp.StpUtil
import com.fund.common.entity.R
import com.fund.modules.user.UserChangePasswordRequest
import com.fund.modules.user.UserLoginRequest
import com.fund.modules.user.UserRegisterRequest
import com.fund.modules.user.service.AppUserService
import com.fund.modules.user.vo.AppLoginInfo
import jakarta.servlet.http.HttpServletRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/user/auth")
class AuthController(
    private val userService: AppUserService
) {

    // 注册
    @SaIgnore
    @PostMapping("register")
    fun register(
        @RequestBody @Validated userRegisterRequest: UserRegisterRequest, request: HttpServletRequest): R<AppLoginInfo> {
        return userService.register(userRegisterRequest, request)
    }

    // 登陆
    @SaIgnore
    @PostMapping("login")
    fun login(@RequestBody @Validated req: UserLoginRequest, request: HttpServletRequest): R<Any> {
        return userService.login(req, request)
    }

    // 退出登录
    @SaIgnore
    @PostMapping(value = ["/logout"])
    fun logout(): R<Unit> {
        StpUtil.logout()
        return R.success()
    }


    // 修改密码
    @SaCheckLogin
    @PostMapping("/changePassword")
    fun changePassword(@RequestBody @Validated req: UserChangePasswordRequest): R<Unit> {
        return userService.changePassword(req, StpUtil.getLoginIdAsLong())
    }

}