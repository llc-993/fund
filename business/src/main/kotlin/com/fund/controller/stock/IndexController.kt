package com.fund.controller.stock

import cn.hutool.http.HttpUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.RedisKeys.STOCK_INDEX
import com.fund.common.entity.R
import com.fund.controller.kline.KlineController
import com.fund.modules.kline.Kline
import com.fund.modules.stock.model.StockIndex
import com.fund.modules.stock.service.StockIndexService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

@Tag(name = "股票指数", description = "股票指数查询相关接口，包括指数行情数据和K线图表")
@RestController
@RequestMapping("index")
class IndexController(
    private val stockIndexService: StockIndexService,
    private val redissonClient: RedissonClient,
) {

    private val logger = KotlinLogging.logger {}

    companion object {

        var URI: String = "http://localhost:9093/kline/chart?symbol=%s&resolution=%s&count=160"
    }

    @Operation(
        summary = "获取股票指数列表",
        description = "获取所有启用的股票指数列表，包括实时行情数据（开盘价、最高价、最低价、当前价、涨跌额、涨跌幅）和K线图表数据（最近160条1分钟K线数据）"
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = [Content(schema = Schema(implementation = StockIndex::class))]
    )
    @GetMapping("")
    fun index(): R<Any> {
        val bucket = redissonClient.getBucket<List<StockIndex>>(STOCK_INDEX)

        if (bucket.isExists) {
            return R.success(bucket.get())
        }

        val stockIndices = stockIndexService.list(
            KtQueryWrapper(StockIndex())
                .eq(StockIndex::status, 1)
        )
        for (stockIndex in stockIndices) {

            val url = String.format(URI, stockIndex.indexId, "P1D")
          //  logger.info { "请求的链接是： $url" }
            val body = HttpUtil.createGet(url).execute().body()

            val parseArray: List<Kline> = JSON.parseArray(body, Kline::class.java)

            // 按 id 降序排序，取第一条数据
            val latestKline = parseArray.sortedByDescending { it.id }.firstOrNull()

            if (latestKline != null && latestKline.open != null && latestKline.close != null) {
                // 计算涨跌额 = 收盘价 - 开盘价
                val chg = latestKline.close!!.subtract(latestKline.open)

                // 计算涨跌幅 = (收盘价 - 开盘价) / 开盘价 * 100
                val chgPct = if (latestKline.open!!.compareTo(BigDecimal.ZERO) != 0) {
                    chg.divide(latestKline.open, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(100))
                } else {
                    BigDecimal.ZERO
                }

                stockIndex.chg = chg
                stockIndex.chgPct = chgPct
                stockIndex.open = latestKline.open
                stockIndex.high = latestKline.high
                stockIndex.low = latestKline.low
                stockIndex.price = latestKline.close
                // 直接使用 parseArray 作为 chart 数据
                stockIndex.chart = parseArray
            }
        }
        bucket.set(stockIndices, 2, TimeUnit.MINUTES)

        return R.success(stockIndices)
    }

}