package com.fund.controller.kline

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.http.HttpUtil
import com.fund.common.entity.R
import com.fund.common.enums.KlineEnum
import com.fund.exception.BusinessException
import com.fund.modules.kline.Kline
import com.fund.modules.kline.service.KlineService
import com.fund.modules.stock.service.StockService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "K线数据", description = "提供股票 K 线历史数据查询接口")
@RestController
@RequestMapping("/kline")
class KlineController(
    private val stockService: StockService,
    private val klineService: KlineService
) {
    private val log = KotlinLogging.logger {}
    companion object {
        const val  DEFAULT_SOURCE_TYPE: String = "investing"

        var URI: String = "http://localhost:9093/kline?symbol=%s&resolution=%s&from=%s&to=%s"
    }


    @Operation(
        summary = "查询股票 K线历史数据",
        description = "根据股票ID、K线周期和时间范围获取外部数据源的 K 线历史记录"
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = [Content(schema = Schema(implementation = Kline::class))]
    )
    @GetMapping("/history")
    fun history(
        @Parameter(description = "股票ID", required = true, example = "1001")
        stockId: Long,
        @Parameter(description = "K线周期：1min、5min、30min、1h、1day、1week、1month", required = true, example = "1h")
        interval: String,
        @Parameter(description = "开始时间（秒或毫秒时间戳）", required = true, example = "1704067200")
        from: Long,
        @Parameter(description = "结束时间（秒或毫秒时间戳）", required = true, example = "1706745600")
        to: Long
    ): R<Any> {

        val stock = stockService.getStockById(stockId) ?: throw BusinessException("stock_not_found")
        log.info("股票: {}", stock.toString())
        if (stock.sourceType.equals(DEFAULT_SOURCE_TYPE)) {
            val investingInterval = KlineEnum.fromInterval(interval)?.investingInterval

            val normalizedFrom = normalizeToSeconds(from)
            val normalizedTo = normalizeToSeconds(to)

            if (normalizedTo <= normalizedFrom) {
                throw BusinessException("invalid_timestamp_range")
            }

            val body = HttpUtil.createGet(
                String.format(URI, stock.pId, investingInterval, normalizedFrom, normalizedTo)
            ).execute().body()
           // log.info("返回的数据是: {}",body.toString())
            return R.success(body, "success")
        }
        return R.success()
    }

    /**
     * 如果时间戳大于等于 1,000,000,000,000，认为是毫秒时间戳并转成秒
     */
    private fun normalizeToSeconds(value: Long): Long {
        return if (value >= 1_000_000_000_000L) value / 1000 else value
    }

    @Operation(summary = "查询本地K线数据", description = "从本地MongoDB查询K线，自动应用用户调控价格")
    @GetMapping("/local")
    fun localHistory(
        @Parameter(description = "股票ID", required = true) stockId: Long,
        @Parameter(description = "K线周期", required = true) interval: String,
        @Parameter(description = "开始时间戳", required = true) from: Long,
        @Parameter(description = "结束时间戳", required = true) to: Long
    ): R<Any> {
        val stock = stockService.getStockById(stockId) ?: throw BusinessException("stock_not_found")
        // 获取当前登录用户ID（如果已登录）
        val userId = try { StpUtil.getLoginIdAsLong() } catch (e: Exception) { null }
        val klines = klineService.getKlinesByTimeRangeForUser(
            symbol = stock.symbol!!,
            market = stock.flag!!,
            interval = interval,
            startTime = normalizeToSeconds(from) * 1000,
            endTime = normalizeToSeconds(to) * 1000,
            userId = userId
        )
        return R.success(klines)
    }
}