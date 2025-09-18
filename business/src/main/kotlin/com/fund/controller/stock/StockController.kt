package com.fund.controller.stock

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.stp.StpUtil
import com.fund.common.entity.R
import com.fund.modules.stock.QueryStockRequest
import com.fund.modules.stock.StockBuyRequest
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.stock.util.StockDataUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/stock")
class StockController(
    private val stockService: StockService,
    private val userPositionService: UserPositionService,
    private val stockDataUtil: StockDataUtil
) {

    /**
     * 股票列表
     */
    @SaIgnore
    @GetMapping("list")
    fun list(req: QueryStockRequest): R<Any> {
        return stockService.list(req)
    }

    @SaIgnore
    @GetMapping("detail")
    fun detail(id: Long): R<Any> {
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

    /**
     * 支持国家列表
     */
    @GetMapping("country-list")
    fun countryList(): R<Any> {
        return stockService.countryList()
    }

    @SaCheckLogin
    @PostMapping("buy")
    fun buy(@RequestBody req: StockBuyRequest, request: HttpServletRequest): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()
        return userPositionService.buy(req, userId, request)
    }

}