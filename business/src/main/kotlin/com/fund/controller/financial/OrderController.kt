package com.fund.controller.financial

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.fund.common.entity.R
import com.fund.modules.financial.FinancialOrderPurchaseRequest
import com.fund.modules.financial.model.FinancialOrder
import com.fund.modules.financial.service.FinancialOrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "理财订单", description = "理财产品申购相关接口")
@RestController
@RequestMapping("/financial/order")
class OrderController(
    private val financialOrderService: FinancialOrderService
) {

    @Operation(
        summary = "申购理财产品",
        description = "用户申购理财产品，需要登录状态"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "申购成功", 
        content = [Content(schema = Schema(implementation = FinancialOrder::class))]
    )
    @ApiResponse(responseCode = "500", description = "申购失败")
    @SaCheckLogin
    @PostMapping("/purchase")
    fun purchase(@RequestBody request: FinancialOrderPurchaseRequest): R<FinancialOrder> {
        val userId = StpUtil.getLoginIdAsLong()
        val order = financialOrderService.purchase(userId, request)
        return R.success(order)
    }
}