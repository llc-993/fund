package com.fund.controller.user

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.Constants
import com.fund.common.entity.R
import com.fund.modules.user.model.AppUser

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import com.fund.modules.user.service.AppUserService
import com.fund.modules.wallet.model.AppUserWalletV2
import com.fund.modules.wallet.service.AppUserWalletV2Service
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "用户信息", description = "用户信息查询相关接口")
@RestController
@RequestMapping("/user")
class UserController(
    private val appUserService: AppUserService,
    private val appUserWalletV2Service: AppUserWalletV2Service
) {

    @Operation(
        summary = "获取用户信息",
        description = "获取当前登录用户的详细信息，包括钱包信息，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = AppUser::class))])
    @SaCheckLogin
    @GetMapping("info")
    fun info(): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()

        val appUser = appUserService.getById(userId)

        val list = appUserWalletV2Service.list(
            KtQueryWrapper(AppUserWalletV2())
                .eq(AppUserWalletV2::userId, userId)
                .eq(AppUserWalletV2::walletType, 0)
        )
        // 使用 find 方法优化查找，避免嵌套循环，同时处理一个 value 对应多个 key 的情况
        list.forEach { wallet ->
            wallet.flag = Constants.MARKET_COIN_MAP.entries
                .find { it.value == wallet.currencyCode }
                ?.key
        }
        appUser.wallet = list

        return R.success(appUser)
    }

}