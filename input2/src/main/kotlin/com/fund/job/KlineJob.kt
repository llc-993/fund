package com.fund.job

import cn.hutool.http.Header
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.config.ApiConfig
import com.fund.enetity.KlineResponse
import com.fund.modules.kline.service.KlineService
import com.fund.modules.kline.util.KlineDataParser
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.service.StockService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class KlineJob(
    private val stockService: StockService,
    private val apiConfig: ApiConfig
) {

    private val logger = KotlinLogging.logger {}
    
    @Autowired
    private lateinit var klineService: KlineService
    
    @Autowired
    private lateinit var klineDataParser: KlineDataParser
    
    // 协程作用域
    private val klineScope = CoroutineScope(Dispatchers.IO)


   // @Scheduled(cron = "0/10 * * * * ?")
    fun loadKline() {
        logger.info("Loading stock kline")
        try {
            val stocks = stockService.list(
                KtQueryWrapper(Stock())
                    .eq(Stock::sourceType, "API")
            )

            if (stocks.isEmpty()) {
                logger.info("No stocks found for kline processing")
                return
            }

            // 使用协程作用域并发处理K线数据
            stocks.forEach { stock ->
                klineScope.launch {
                    processStockKline(stock)
                }
            }
            
            logger.info("Completed processing kline data for ${stocks.size} stocks")
        } catch (e: Exception) {
            logger.error(e) { "Error while loading kline" }
        }
    }
    
    /**
     * 处理单个股票的K线数据
     */
    private suspend fun processStockKline(stock: Stock) {
        try {
            // 支持的时间间隔
            val intervals = listOf("1min", "5min", "30min", "1h", "1day", "1week", "1month")
            
            for (interval in intervals) {
                processKlineForInterval(stock, interval)
            }
            
        } catch (e: Exception) {
            logger.error(e) { "Error processing kline for stock: ${stock.symbol}" }
        }
    }
    
    /**
     * 处理指定时间间隔的K线数据（支持分页）
     */
    private suspend fun processKlineForInterval(stock: Stock, interval: String) {
        try {
            var page = 1
            var hasMoreData = true
            
            while (hasMoreData) {
                val url = apiConfig.stock.getMiniListUrl(stock.flag!!, stock.symbol!!, interval, page.toString())

                val httpRequest = HttpUtil.createGet(url)
                httpRequest.header(Header.AUTHORIZATION, apiConfig.stock.authorization)

                val body = httpRequest.execute().body()
                
                if (body.isNullOrBlank()) {
                    logger.warn("Empty response for stock: ${stock.symbol}, interval: $interval, page: $page")
                    hasMoreData = false
                    continue
                }

                // 解析API响应
                val response = JSON.parseObject(body, KlineResponse::class.java)
                
                if (response.status != 0) {
                    logger.warn("API error for stock ${stock.symbol}, interval: $interval, page: $page: ${response.message}")
                    hasMoreData = false
                    continue
                }
                
                val dataString = response.data
                if (dataString.isNullOrBlank()) {
                    hasMoreData = false
                    continue
                }
                
                // 解析K线数据
                val klines = klineDataParser.parseKlineData(
                    dataString, 
                    stock.symbol!!, 
                    stock.flag!!, 
                    interval
                )
                
                if (klines.isNotEmpty()) {
                    // 保存到MongoDB
                    klineService.saveKlines(klines)
                    page++
                } else {
                    hasMoreData = false
                }
            }
            
        } catch (e: Exception) {
            logger.error(e) { "Error processing kline for stock: ${stock.symbol}, interval: $interval" }
        }
    }


}