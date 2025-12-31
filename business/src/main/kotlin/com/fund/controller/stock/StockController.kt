package com.fund.controller.stock

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.RedisKeys.RISE_STOCK
import com.fund.common.RedisKeys.STOCK_KEY
import com.fund.common.entity.R
import com.fund.modules.stock.QueryStockRequest
import com.fund.modules.stock.StockBuyRequest
import com.fund.modules.stock.StockAddOrderRequest
import com.fund.modules.stock.UpdateProfitTargetRequest
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.model.UserPosition
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
import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.concurrent.TimeUnit


@Tag(name = "股票交易", description = "股票查询、买入、卖出、挂单等相关接口")
@RestController
@RequestMapping("/stock")
class StockController(
    private val stockService: StockService,
    private val userPositionService: UserPositionService,
    private val userPendingOrderService: UserPendingOrderService,
    private val stockDataUtil: StockDataUtil,
    private val redissonClient: RedissonClient
) {

    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "股票列表",
        description = "查询股票列表，支持分页和筛选"
    )
    @ApiResponse(
        responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = Stock::class))]
    )
    @SaIgnore
    @GetMapping("list")
    fun list(@ModelAttribute req: QueryStockRequest): R<Any> {
        return stockService.list(req)
    }

    @Operation(
        summary = "获取所有上涨股票列表",
        description = "查询所有上涨股票列表"
    )
    @ApiResponse(
        responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = Stock::class))]
    )
    @SaIgnore
    @GetMapping("getRiseStock")
    fun getRiseStock(): R<Any> {
        val stockCache = redissonClient.getList<Stock>(RISE_STOCK)

        if (stockCache.isExists) {
            return R.success(stockCache.readAll())
        }

        val keys = redissonClient.keys
        val matchedKeys = keys.getKeysByPattern("$STOCK_KEY*")
        val list = mutableListOf<Stock>()
        for (key in matchedKeys) {
            try {
                val bucket = redissonClient.getBucket<String>(key)
                val cachedJson = bucket.get()
                val cachedStock = JSON.parseObject(cachedJson, Stock::class.java)
                if (cachedStock.chgPct == null) {
                    continue
                }
                if (cachedStock.chgPct!!.compareTo(BigDecimal.ZERO) > 0) {
                    list.add(cachedStock)
                }
            } catch (e: Exception) {
                logger.error(e) { "Error in getting stock" }
                continue
            }
        }

        val sortedList = list.sortedWith(compareByDescending<Stock> { it.chgPct }).take(20)

        for (stock in sortedList) {
            stockCache.add(stock)
        }
        stockCache.expire(3, TimeUnit.MINUTES)

        return R.success(sortedList)
    }


    @Operation(
        summary = "股票详情",
        description = "根据ID获取股票详细信息，包括实时行情数据"
    )
    @ApiResponse(
        responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = Stock::class))]
    )
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
    @ApiResponse(
        responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = String::class))]
    )
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

    @Operation(
        summary = "用户持仓信息"
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(schema = Schema(implementation = UserPosition::class))]
    )
    @SaCheckLogin
    @GetMapping("userPosition")
    fun userPosition(
        @Parameter(description = "交易对", required = false)
        @RequestParam(value = "symbol", required = false) symbol: String?
    ): R<Any> {
        val userId = StpUtil.getLoginIdAsLong()

        val list = userPositionService.list(
            KtQueryWrapper(UserPosition())
                .eq(UserPosition::userId, userId)
                .eq(StrUtil.isNotBlank(symbol), UserPosition::stockCode, symbol)
                .orderByDesc(UserPosition::id)
        )
        for (position in list) {
            position.price = when {
                // 优先使用 stockGid 查询
                StrUtil.isNotBlank(position.stockGid) && position.stockGid != "null" -> {
                    val stock = stockService.getStockById(position.stockGid!!.toLong())
                    stock?.last ?: BigDecimal.ZERO
                }
                // 否则使用股票代码、类型、名称组合查询
                else -> {
                    val stock = stockService.getOne(
                        KtQueryWrapper(Stock())
                            .eq(Stock::symbol, position.stockCode)
                            .eq(Stock::flag, position.stockType)
                            .eq(Stock::name, position.stockName)
                            .orderByDesc(Stock::id)
                            .last("limit 1")
                    )
                    stock?.last ?: BigDecimal.ZERO
                }
            }
        }
        return R.success(list)
    }

}