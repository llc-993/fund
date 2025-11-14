package com.fund.controller.financial

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.fund.common.entity.R
import com.fund.modules.financial.FinancialOrderPurchaseRequest
import com.fund.modules.financial.FinancialOrderRedeemRequest
import com.fund.modules.financial.model.FinancialOrder
import com.fund.modules.financial.service.FinancialOrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "理财订单", description = "理财产品申购、赎回相关接口")
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




    @Operation(
        summary = "赎回理财产品",
        description = "用户赎回理财产品，将订单状态更新为已平仓，并将累计收益解冻到可用余额"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "赎回成功", 
        content = [Content(schema = Schema(implementation = FinancialOrder::class))]
    )
    @ApiResponse(responseCode = "500", description = "赎回失败")
    @SaCheckLogin
    @PostMapping("/redeem")
    fun redeem(@RequestBody request: FinancialOrderRedeemRequest): R<FinancialOrder> {
        val userId = StpUtil.getLoginIdAsLong()
        val order = financialOrderService.redeem(userId, request)
        return R.success(order)
    }
    
    @Operation(
        summary = "获取用户理财购买记录",
        description = "获取当前登录用户的所有理财购买记录列表，按创建时间降序排序，不分页"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "查询成功", 
        content = [Content(schema = Schema(implementation = FinancialOrder::class))]
    )
    @SaCheckLogin
    @GetMapping("/list")
    fun getUserOrders(): R<List<FinancialOrder>> {
        val userId = StpUtil.getLoginIdAsLong()
        val orders = financialOrderService.getUserOrders(userId)
        return R.success(orders)
    }
}