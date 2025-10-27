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

@Tag(name = "充值管理", description = "用户充值申请和历史记录相关接口")
@RestController
@RequestMapping("/deposit")
class DepositController(
    private val cashInOrderService: AppUserCashInOrderService
) {

    @Operation(
        summary = "充值申请",
        description = "用户提交充值申请，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "申请成功")
    @SaCheckLogin
    @PostMapping("/request")
    fun request(@RequestBody @Validated req: CashInReq): R<Any> {
        return cashInOrderService.request(StpUtil.getLoginIdAsLong(), req)
    }

    @Operation(
        summary = "充值历史记录",
        description = "查询用户的充值历史记录，支持分页，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SaCheckLogin
    @GetMapping("/history")
    fun history(@Parameter(hidden = true) req: PageReq): R<Page<AppUserCashInOrder>> {
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