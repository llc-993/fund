package com.fund.controller.wallet

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.modules.wallet.model.AppUserWalletV2
import com.fund.modules.wallet.service.AppUserWalletV2Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/wallet")
class WalletController(
    private val walletV2Service: AppUserWalletV2Service
) {

    @SaCheckLogin
    @GetMapping("list")
    fun list(): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()
        val list = walletV2Service.list(
            KtQueryWrapper(AppUserWalletV2())
                .eq(AppUserWalletV2::userId, userId)
        )
        return R.success(list)
    }

}