package com.fund.controller.auth

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.StrUtil
import com.fund.common.Constants
import com.fund.common.entity.R
import com.fund.modules.user.UserChangePasswordRequest
import com.fund.modules.user.UserLoginRequest
import com.fund.modules.user.UserRegisterRequest
import com.fund.modules.user.UserUpdateRequest
import com.fund.modules.user.service.AppUserService
import com.fund.modules.user.vo.AppLoginInfo
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.servlet.http.HttpServletRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Tag(name = "用户认证", description = "用户注册、登录、修改密码等认证相关接口")
@RestController
@RequestMapping("/user/auth")
class AuthController(
    private val userService: AppUserService
) {

    @Operation(
        summary = "用户注册",
        description = "新用户注册接口，返回用户信息和登录 Token"
    )
    @ApiResponse(responseCode = "200", description = "注册成功",
        content = [Content(schema = Schema(implementation = AppLoginInfo::class))])
    @SaIgnore
    @PostMapping("register")
    fun register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "用户注册信息",
            required = true,
            content = [
                Content(schema = Schema(implementation = UserRegisterRequest::class))
            ]
        ) @RequestBody @Validated userRegisterRequest: UserRegisterRequest,
        @Parameter(hidden = true) request: HttpServletRequest
    ): R<AppLoginInfo> {
        return userService.register(userRegisterRequest, request)
    }

    @Operation(
        summary = "用户登录",
        description = "用户登录接口，返回用户信息和登录 Token"
    )
    @ApiResponse(responseCode = "200", description = "登录成功",
        content = [Content(schema = Schema(implementation = AppLoginInfo::class))])
    @SaIgnore
    @PostMapping("login")
    fun login(
        @SwaggerRequestBody(description = "用户登录信息", required = true) @RequestBody @Validated req: UserLoginRequest,
        @Parameter(hidden = true) request: HttpServletRequest
    ): R<Any> {
        return userService.login(req, request)
    }

    @Operation(
        summary = "退出登录",
        description = "用户退出登录，清除登录状态"
    )
    @ApiResponse(responseCode = "200", description = "退出成功")
    @SaIgnore
    @PostMapping(value = ["/logout"])
    fun logout(): R<Unit> {
        StpUtil.logout()
        return R.success()
    }

    @Operation(
        summary = "修改密码",
        description = "用户修改登录密码，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "修改成功")
    @SaCheckLogin
    @PostMapping("/changePassword")
    fun changePassword(
        @SwaggerRequestBody(description = "修改密码信息", required = true) @RequestBody @Validated req: UserChangePasswordRequest
    ): R<Unit> {
        return userService.changePassword(req, StpUtil.getLoginIdAsLong())
    }

    @SaCheckLogin
    @Operation(summary ="校验交易密码")
    @PostMapping("/checkMoneyPassword")
    fun checkMoneyPassword(@Parameter(name = "moneyPassword",description= "交易密码", required = true) @RequestParam("moneyPassword") moneyPassword: String): R<Any>{
        val userId = StpUtil.getLoginIdAsLong()

        val user = userService.getById(userId)

        if (StrUtil.isBlank(user.showMoneyPassword)) {
            return R.success("2", "SUCCESS",  200)
        }

        if (StrUtil.isBlank(moneyPassword)) {
            return R.success(0)
        }

        return R.success(if (user.showMoneyPassword.equals(moneyPassword)) 1 else 0)
    }

    /**
     * 修改交易密码
     * @param req
     * @return
     */
    @Operation(summary ="修改交易密码")
    @PostMapping("/changeMoneyPassword")
    @SaCheckLogin
    fun changeMoneyPassword(@SwaggerRequestBody(description = "修改交易密码", required = true)  @RequestBody @Validated req: UserChangePasswordRequest): R<Unit> {
        val userId = StpUtil.getLoginIdAsLong()
        userService.changeMoneyPassword(req, userId)
        return R.success()
    }

    /**
     * 修改kyc信息
     */
    @Operation(summary = "修改kyc信息")
    @PostMapping("updateKyc")
    @SaCheckLogin
    fun updateKyc(@SwaggerRequestBody(description = "kyc参数", required = true)  @RequestBody @Validated req: UserUpdateRequest): R<Unit> {
        return userService.updateKyc(req, StpUtil.getLoginIdAsLong())
    }

}