package com.fund.controller.financial

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.modules.financial.FinancialOrderForceRedeemRequest
import com.fund.modules.financial.FinancialOrderQueryRequest
import com.fund.modules.financial.model.FinancialOrder
import com.fund.modules.financial.service.FinancialOrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "理财订单管理", description = "理财订单管理相关接口")
@RestController
@RequestMapping("/financial/order/manage")
class OrderManageController(
    private val financialOrderService: FinancialOrderService
) {

    @Operation(
        summary = "查询理财订单列表",
        description = "分页查询理财订单列表，可按用户ID、产品ID、订单状态等筛选"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "查询成功", 
        content = [Content(schema = Schema(implementation = FinancialOrder::class))]
    )
    @PostMapping("/list")
    fun list(@RequestBody request: FinancialOrderQueryRequest): R<Page<FinancialOrder>> {
        val page = financialOrderService.pageQuery(request)
        return R.success(page)
    }
    
    @Operation(
        summary = "强制赎回订单",
        description = "管理员强制赎回指定订单，将订单状态更新为已平仓，并将累计收益和本金返还给用户"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "赎回成功", 
        content = [Content(schema = Schema(implementation = FinancialOrder::class))]
    )
    @PostMapping("/force-redeem")
    fun forceRedeem(@RequestBody request: FinancialOrderForceRedeemRequest): R<FinancialOrder> {
        val order = financialOrderService.forceRedeem(request)
        return R.success(order)
    }
    
    @Operation(
        summary = "获取订单详情",
        description = "根据订单ID获取理财订单详情"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "查询成功", 
        content = [Content(schema = Schema(implementation = FinancialOrder::class))]
    )
    @GetMapping("/info/{id}")
    fun getInfo(@PathVariable id: Long): R<FinancialOrder> {
        val order = financialOrderService.getById(id)
            ?: return R.error("订单不存在")
        return R.success(order)
    }
}
