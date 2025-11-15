package com.fund.controller.kline

import cn.hutool.http.HttpUtil
import com.fund.common.entity.R
import com.fund.common.enums.KlineEnum
import com.fund.exception.BusinessException
import com.fund.modules.kline.Kline
import com.fund.modules.stock.service.StockService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "K线数据", description = "提供股票 K 线历史数据查询接口")
@RestController
@RequestMapping("/kline")
class KlineController(
    private val stockService: StockService
) {

    companion object {
        const val  DEFAULT_SOURCE_TYPE: String = "investing"

        var URI: String = "http://localhost:9092/kline?symbol=%s&resolution=%s&from=%s&to=%s"
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
            return R.success(body)
        }
        return R.success()
    }

    /**
     * 如果时间戳大于等于 1,000,000,000,000，认为是毫秒时间戳并转成秒
     */
    private fun normalizeToSeconds(value: Long): Long {
        return if (value >= 1_000_000_000_000L) value / 1000 else value
    }
}