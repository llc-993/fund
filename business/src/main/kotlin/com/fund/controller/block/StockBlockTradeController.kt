package com.fund.controller.block

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.modules.block.BlockTradeApplyRequest
import com.fund.modules.block.BlockTradeUpdateRequest
import com.fund.modules.block.model.StockBlockTrade
import com.fund.modules.block.model.StockBlockTradeSubscription
import com.fund.modules.block.service.StockBlockTradeService
import com.fund.modules.block.service.StockBlockTradeSubscriptionService
import com.fund.modules.stock.service.StockService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

/**
 * 大宗交易
 */
@Tag(name = "大宗交易", description = "股票大宗交易列表、申购、历史记录等接口")
@RestController
@RequestMapping("/block")
class StockBlockTradeController(
    private val stockBlockTradeService: StockBlockTradeService,
    private val stockBlockTradeSubscriptionService: StockBlockTradeSubscriptionService,
    private val stockService: StockService
) {

    @Operation(
        summary = "获取大宗交易列表",
        description = "获取所有可申购的大宗交易列表"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = StockBlockTrade::class))])
    @GetMapping("list")
    fun list(): R<Any> {
        val blockTrades = stockBlockTradeService.list(
            KtQueryWrapper(StockBlockTrade())
                .eq(StockBlockTrade::status, 1)
        )

        for (stockBlockTrade in blockTrades) {
            if (stockBlockTrade.stockId == null) continue
            stockBlockTrade.price = stockService.getStockById(stockBlockTrade.stockId!!).last ?: BigDecimal.ZERO
        }
        return R.success(blockTrades)
    }

    @Operation(
        summary = "申请申购大宗交易",
        description = "用户申请申购大宗交易，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "申请成功",
            content = [Content(schema = Schema(implementation = R::class))])
    @SaCheckLogin
    @PostMapping("apply")
    fun apply(@RequestBody req: BlockTradeApplyRequest): R<Any> {
        return stockBlockTradeSubscriptionService.apply(req, StpUtil.getLoginIdAsLong())
    }

    @Operation(
        summary = "获取申购历史",
        description = "获取当前用户的大宗交易申购历史记录，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = StockBlockTradeSubscription::class))])
    @SaCheckLogin
    @GetMapping("history")
    fun history(): R<Any> {
        return stockBlockTradeSubscriptionService.history(StpUtil.getLoginIdAsLong())
    }

    @Operation(
        summary = "修改申购信息",
        description = "修改大宗交易申购信息，需要登录状态"
    )
    @ApiResponse(responseCode = "200", description = "修改成功",
        content = [Content(schema = Schema(implementation = R::class))])
    @SaCheckLogin
    @PostMapping("update")
    fun update(@RequestBody req: BlockTradeUpdateRequest): R<Any> {
        return stockBlockTradeSubscriptionService.update(req, StpUtil.getLoginIdAsLong())
    }

}