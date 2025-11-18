package com.fund.controller.cash

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.modules.cash.CashInOrderQueryPageReq
import com.fund.modules.cash.CashInReviewReq
import com.fund.modules.sys.service.SysOptLogService
import com.fund.modules.wallet.model.AppUserCashInOrder
import com.fund.modules.wallet.service.AppUserCashInOrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 充值管理
 */
@RestController
@RequestMapping("/cashIn")
@Tag(name = "充值管理", description = "充值管理相关接口")
class CashInController(
    private val cashInOrderService: AppUserCashInOrderService,
    private val optLogService: SysOptLogService
) {

    @Operation(summary = "充值列表", description = "和刷单接口一样")
    @GetMapping("/page")
    fun page(req: CashInOrderQueryPageReq): R<Page<AppUserCashInOrder>> {
        val p: Page<AppUserCashInOrder> = Page<AppUserCashInOrder>(req.pageNum, req.pageSize)
        val page: Page<AppUserCashInOrder> = cashInOrderService.page<Page<AppUserCashInOrder>>(
            p,
            KtQueryWrapper(AppUserCashInOrder())
                .like(StrUtil.isNotBlank(req.userAccount), AppUserCashInOrder::userAccount, req.userAccount)
                .eq(StrUtil.isNotBlank(req.orderNo), AppUserCashInOrder::orderNo, req.orderNo)
                .eq(req.cashStatus != null, AppUserCashInOrder::cashStatus, req.cashStatus)
                .gt(StrUtil.isNotBlank(req.startTime), AppUserCashInOrder::applyTime, req.startTime)
                .le(StrUtil.isNotBlank(req.endTime), AppUserCashInOrder::applyTime, req.endTime)
                .orderByDesc(AppUserCashInOrder::applyTime)
        )
        return R.success(page)
    }


    @PostMapping("/review")
    @Operation(summary = "充值审核", description = "和刷单接口一样")
    fun review(@RequestBody @Validated req: CashInReviewReq): R<Any> {
        val adminId = StpUtil.getLoginIdAsLong()
        cashInOrderService.review(req)
        optLogService.addLog(adminId, "充值审核", JSON.toJSONString(req))
        return R.success()
    }

}