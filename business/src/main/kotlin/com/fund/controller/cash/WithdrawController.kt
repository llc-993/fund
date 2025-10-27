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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "提现管理", description = "用户提现申请和历史记录相关接口")
@RestController
@RequestMapping(value = ["/withdraw"])
class WithdrawController(
    private val appUserCashOutOrderService: AppUserCashOutOrderService
) {

    @Operation(
        summary = "提现申请",
        description = "用户提交提现申请，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "申请成功")
    @SaCheckLogin
    @PostMapping("/request")
    fun request(@RequestBody @Validated req: CashOutReq): R<Any> {
        return appUserCashOutOrderService.request(StpUtil.getLoginIdAsLong(), req)
    }

    @Operation(
        summary = "提现历史记录",
        description = "查询用户的提现历史记录，支持分页，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SaCheckLogin
    @GetMapping("/history")
    fun history(@Parameter(hidden = true) req: PageReq): R<Any> {
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