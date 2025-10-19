package com.fund.controller.user

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.modules.user.service.AppUserService
import com.fund.modules.wallet.model.AppUserWalletV2
import com.fund.modules.wallet.service.AppUserWalletV2Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/user")
class UserController(
    private val appUserService: AppUserService,
    private val appUserWalletV2Service: AppUserWalletV2Service
) {

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
        appUser.wallet = list

        return R.success(appUser)
    }

}