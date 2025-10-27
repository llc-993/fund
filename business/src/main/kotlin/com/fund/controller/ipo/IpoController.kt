package com.fund.controller.ipo

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.modules.ipo.IpoApplyRequest
import com.fund.modules.ipo.IpoUpdateRequest
import com.fund.modules.ipo.model.Ipo
import com.fund.modules.ipo.service.IpoService
import com.fund.modules.ipo.service.StockSubscriptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "IPO申购", description = "IPO列表、申购、历史记录等接口")
@RestController
@RequestMapping("/ipo")
class IpoController(
    private val ipoService: IpoService,
    private val stockSubscriptionService: StockSubscriptionService
) {

    @Operation(
        summary = "获取IPO列表",
        description = "获取所有可申购的IPO列表"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("list")
    fun list(): R<Any> {
        val ipos = ipoService.list(
            KtQueryWrapper(Ipo())
                .eq(Ipo::status, 1)
        )
        return R.success(ipos)
    }

    @Operation(
        summary = "申请申购IPO",
        description = "用户申请申购IPO，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "申请成功")
    @SaCheckLogin
    @PostMapping("apply")
    fun apply(@RequestBody req: IpoApplyRequest): R<Any> {
        return stockSubscriptionService.apply(req, StpUtil.getLoginIdAsLong())
    }

    @Operation(
        summary = "获取申购历史",
        description = "获取当前用户的IPO申购历史记录，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SaCheckLogin
    @GetMapping("history")
    fun history(): R<Any> {
        return stockSubscriptionService.history(StpUtil.getLoginIdAsLong())
    }

    @Operation(
        summary = "修改申购信息",
        description = "修改IPO申购信息，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "修改成功")
    @SaCheckLogin
    @PostMapping("update")
    fun update(@RequestBody req: IpoUpdateRequest): R<Any> {
        return stockSubscriptionService.update(req, StpUtil.getLoginIdAsLong())
    }

}