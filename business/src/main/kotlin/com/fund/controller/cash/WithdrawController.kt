package com.fund.controller.cash

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.modules.cash.CashOutReq
import com.fund.modules.wallet.model.AppUserCashOutOrder
import com.fund.modules.wallet.service.AppUserCashOutOrderService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/withdraw"])
class WithdrawController(
    private val appUserCashOutOrderService: AppUserCashOutOrderService
) {


    /**
     * 提现申请
     */
    @SaCheckLogin
    @PostMapping("/request")
    fun request(@RequestBody @Validated req: CashOutReq): R<Any> {
        return appUserCashOutOrderService.request(StpUtil.getLoginIdAsLong(), req)
    }

    @SaCheckLogin
    @GetMapping("/history")
    fun history(req: PageReq): R<Any> {
        val page1: Page<AppUserCashOutOrder> = Page(req.pageNum, req.pageSize)

        val page = appUserCashOutOrderService.page(
            page1,
            KtQueryWrapper(AppUserCashOutOrder())
                .eq(AppUserCashOutOrder::userId, StpUtil.getLoginIdAsLong())
                .orderByDesc(AppUserCashOutOrder::id)
        )
        return R.success(page)
    }

}