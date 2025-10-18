package com.fund.controller.cash

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.modules.cash.CashInReq
import com.fund.modules.wallet.model.AppUserCashInOrder
import com.fund.modules.wallet.service.AppUserCashInOrderService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/deposit")
class DepositController(
    private val cashInOrderService: AppUserCashInOrderService
) {

    /**
     * 充值申请
     */
    @SaCheckLogin
    @PostMapping("/request")
    fun request(@RequestBody @Validated req: CashInReq): R<Any> {
        return cashInOrderService.request(StpUtil.getLoginIdAsLong(), req)
    }

    /**
     * 充值历史记录
     */
    @GetMapping("/history")
    fun history(req: PageReq): R<Page<AppUserCashInOrder>> {
        val userId = StpUtil.getLoginIdAsLong()
        val p = Page<AppUserCashInOrder>(req.pageNum, req.pageSize)
        val page = cashInOrderService.page(
            p,
            KtQueryWrapper(AppUserCashInOrder())
                .eq(AppUserCashInOrder::userId, userId)
                .orderByDesc(AppUserCashInOrder::applyTime)
        )
        return R.success(page)
    }
}