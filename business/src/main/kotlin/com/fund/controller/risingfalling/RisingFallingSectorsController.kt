package com.fund.controller.risingfalling

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import com.fund.modules.risingFalling.RisingFallingSectorsApplyRequest
import com.fund.modules.risingFalling.RisingFallingSectorsUpdateRequest
import com.fund.modules.risingFalling.model.RisingFallingSectors
import com.fund.modules.risingFalling.model.RisingFallingSectorsSubscription
import com.fund.modules.risingFalling.service.RisingFallingSectorsService
import com.fund.modules.risingFalling.service.RisingFallingSectorsSubscriptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 涨跌板块
 */
@Tag(name = "涨跌板块", description = "涨跌板块列表、申购、历史记录等接口")
@RestController
@RequestMapping("/risingFallingSectors")
class RisingFallingSectorsController(
    private val risingFallingSectorsService: RisingFallingSectorsService,
    private val risingFallingSectorsSubscriptionService: RisingFallingSectorsSubscriptionService
) {

    @Operation(
        summary = "获取涨跌板块列表",
        description = "获取所有可申购的涨跌板块列表"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = RisingFallingSectors::class))])
    @GetMapping("list")
    fun list(): R<Any> {
        val risingFallingSectors = risingFallingSectorsService.list(
            KtQueryWrapper(RisingFallingSectors())
                .eq(RisingFallingSectors::displayStatus, 0)  // 0=显示
        )
        return R.success(risingFallingSectors)
    }

    @Operation(
        summary = "申请申购涨跌板块",
        description = "用户申请申购涨跌板块，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "申请成功")
    @SaCheckLogin
    @PostMapping("apply")
    fun apply(@RequestBody req: RisingFallingSectorsApplyRequest): R<Any> {
        return risingFallingSectorsSubscriptionService.apply(req, StpUtil.getLoginIdAsLong())
    }

    @Operation(
        summary = "获取申购历史",
        description = "获取当前用户的涨跌板块申购历史记录，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = RisingFallingSectorsSubscription::class))])
    @SaCheckLogin
    @GetMapping("history")
    fun history(): R<Any> {
        return risingFallingSectorsSubscriptionService.history(StpUtil.getLoginIdAsLong())
    }

    @Operation(
        summary = "修改申购信息",
        description = "修改涨跌板块申购信息，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "修改成功")
    @SaCheckLogin
    @PostMapping("update")
    fun update(@RequestBody req: RisingFallingSectorsUpdateRequest): R<Any> {
        return risingFallingSectorsSubscriptionService.update(req, StpUtil.getLoginIdAsLong())
    }

}
