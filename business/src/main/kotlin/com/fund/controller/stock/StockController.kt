package com.fund.controller.stock

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.stp.StpUtil
import com.fund.common.entity.R
import com.fund.modules.stock.QueryStockRequest
import com.fund.modules.stock.StockBuyRequest
import com.fund.modules.stock.StockAddOrderRequest
import com.fund.modules.stock.UpdateProfitTargetRequest
import com.fund.modules.stock.model.Stock
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.service.UserPendingOrderService
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.stock.util.StockDataUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Tag(name = "股票交易", description = "股票查询、买入、卖出、挂单等相关接口")
@RestController
@RequestMapping("/stock")
class StockController(
    private val stockService: StockService,
    private val userPositionService: UserPositionService,
    private val userPendingOrderService: UserPendingOrderService,
    private val stockDataUtil: StockDataUtil
) {

    @Operation(
        summary = "股票列表",
        description = "查询股票列表，支持分页和筛选"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = Stock::class))])
    @SaIgnore
    @GetMapping("list")
    fun list(@ModelAttribute req: QueryStockRequest): R<Any> {
        return stockService.list(req)
    }

    @Operation(
        summary = "股票详情",
        description = "根据ID获取股票详细信息，包括实时行情数据"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = Stock::class))])
    @SaIgnore
    @GetMapping("detail")
    fun detail(
        @Parameter(description = "股票ID", required = true) @RequestParam id: Long
    ): R<Any> {
        try {
            // 获取股票基本信息
            val stock = stockService.getStockById(id)

            // 获取完整的StockData信息
            val stockData = stockDataUtil.getFullStockData(stock)

            // 构建响应Map
            val response = mutableMapOf<String, Any>()
            response["stock"] = stock
            if (stockData != null) {
                response["stockData"] = stockData
            }

            return R.success(response)
        } catch (e: Exception) {
            return R.error()
        }
    }

    @Operation(
        summary = "支持的国家列表",
        description = "获取系统支持的所有国家列表"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = String::class))])
    @GetMapping("country-list")
    fun countryList(): R<Any> {
        return stockService.countryList()
    }

    @Operation(
        summary = "买入股票",
        description = "用户买入股票，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "买入成功")
    @SaCheckLogin
    @PostMapping("buy")
    fun buy(
        @RequestBody req: StockBuyRequest,
        @Parameter(hidden = true) request: HttpServletRequest
    ): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()
        return userPositionService.buy(req, userId, request)
    }

    @Operation(
        summary = "卖出股票",
        description = "用户卖出股票，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "卖出成功")
    @SaCheckLogin
    @PostMapping("sell")
    fun sell(
        @Parameter(description = "持仓编号", required = true) @RequestParam positionSn: String
    ): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()
        return userPositionService.sell(positionSn, userId, 1, "11")
    }

    @Operation(
        summary = "修改盈利目标",
        description = "用户修改股票的盈利目标价格，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "修改成功")
    @SaCheckLogin
    @PostMapping("update-profit-target")
    fun updateProfitTarget(@RequestBody req: UpdateProfitTargetRequest): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()
        return userPositionService.updateProfitTarget(req, userId)
    }

    @Operation(
        summary = "添加挂单",
        description = "添加股票挂单（买入或卖出），需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "添加成功")
    @SaCheckLogin
    @PostMapping("add-order")
    fun addOrder(@RequestBody req: StockAddOrderRequest): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()
        return userPendingOrderService.addOrder(req, userId)
    }

    @Operation(
        summary = "删除挂单",
        description = "删除股票挂单，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SaCheckLogin
    @PostMapping("del-order")
    fun delOrder(
        @Parameter(description = "挂单ID", required = true) @RequestParam id: Long
    ): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()
        return userPendingOrderService.delOrder(id, userId)
    }

}