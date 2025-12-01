package com.fund.controller.wallet

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.Constants
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.modules.wallet.model.AppUserWalletV2
import com.fund.modules.wallet.model.AppWalletOperationLog
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.modules.wallet.service.AppWalletOperationLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "钱包管理", description = "用户钱包查询相关接口")
@RestController
@RequestMapping("/wallet")
class WalletController(
    private val walletV2Service: AppUserWalletV2Service,
    private val appWalletOperationLogService: AppWalletOperationLogService
) {

    @Operation(
        summary = "获取钱包列表",
        description = "获取当前用户的所有钱包信息，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = AppUserWalletV2::class))])
    @SaCheckLogin
    @GetMapping("list")
    fun list(): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()
        val list = walletV2Service.list(
            KtQueryWrapper(AppUserWalletV2())
                .eq(AppUserWalletV2::userId, userId)
        )
        // 使用 find 方法优化查找，避免嵌套循环，同时处理一个 value 对应多个 key 的情况
        list.forEach { wallet ->
            wallet.flag = Constants.MARKET_COIN_MAP.entries
                .find { it.value == wallet.currencyCode }
                ?.key
        }

        return R.success(list)
    }

    @Operation(
        summary = "获取钱包流水记录",
        description = "获取钱包流水记录信息，需要登录状态"
    )
    @SaCheckLogin
    @GetMapping("walletOperationList")
    fun walletOperationList(@SwaggerRequestBody(description = "修改密码信息", required = true) @RequestBody req: PageReq): R<Any> {
        val page = Page<AppWalletOperationLog>(req.pageNum, req.pageSize)

        val page1 = appWalletOperationLogService.page(
            page, KtQueryWrapper(AppWalletOperationLog())
                .eq(AppWalletOperationLog::userId, StpUtil.getLoginIdAsLong())
                .orderByDesc(AppWalletOperationLog::id)
        )
        return R.success(page1)
    }

}