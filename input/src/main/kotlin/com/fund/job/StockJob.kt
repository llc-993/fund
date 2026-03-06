package com.fund.job

// https://api.investing.com/api/financialdata/8883/historical/chart/?interval=PT15M&pointscount=160
// https://tvc4.investing.com/1fac091226d4dbe226e4b1ae3f802bc2/1770012769/6/6/28/history?symbol=8883&resolution=M&from=836892789&to=1770012849
// https://widget2.sentryd.com/widget/sentry/api/Pricing
// https://widget2.sentryd.com/widget/api/instruments/latest/EUR_USD,USD_JPY,GBP_USD,USD_CHF,USD_CAD,AUD_USD,NZD_USD,EUR_CAD,EUR_CHF,EUR_GBP,EUR_JPY,EUR_AUD,GBP_JPY,USD_CNH,USD_ILS,USD_MXN,USD_TRY,XAU_USD?ts=1770014637527

import com.fund.investing.WsStarter
import com.fund.modules.stock.adapter.StockDataProcessor
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.service.StockService
import com.fund.common.Constants
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.fund.investing.InvestingClient
import com.fund.modules.stock.consumer.PositionUserUpdListener
import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Component
class StockJob(
    private var investingClient: InvestingClient,
    private val positionUserUpdListener: PositionUserUpdListener
) {
    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var stockService: StockService

    @Autowired
    private lateinit var wsStarter: WsStarter

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    private lateinit var redissonClient: RedissonClient

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private lateinit var stockProcessingExecutor: ThreadPoolTaskExecutor

    @Autowired
    private lateinit var stockDataProcessor: StockDataProcessor


   // @Scheduled(cron = "1 * * * * ?")
    fun loadStocks() {
        logger.info("Loading stocks...")
        val countryMarketStatus = mutableMapOf<String, String>()

        Constants.MARKET_LIST.forEach { countryId ->
            var page = 0
            val pageSize = InvestingClient.DEFAULT_PAGE_SIZE
            var total = Int.MAX_VALUE
            var hasOpenMarket = false

            while (page * pageSize < total) {

                val url = String.format(Constants.API_URL_TEMPLATE, countryId, page, pageSize)
                logger.info("请求的链接：$url")
                val json = investingClient.loadData(url)

                val jsonObject = JSON.parseObject(json)
                total = jsonObject.getInteger("total") ?: 0

                val dataArray = jsonObject.getJSONArray("data")
                if (dataArray != null && dataArray.isNotEmpty()) {
                    // 使用线程池并发处理股票数据
                    val futures = mutableListOf<CompletableFuture<Boolean>>()

                    dataArray.forEach { item ->
                        val future = CompletableFuture.supplyAsync({
                            try {
                                val jsonObject = item as JSONObject
                                val pId = jsonObject.getLong("Id")
                                val symbol = jsonObject.getString("Symbol")

                                // 使用适配器处理数据
                                val stock = parseStockFromJson(jsonObject)

                                if (stock != null) {
                                    stock.sourceType = "investing"

                                    stockService.upsertById(stock)
                                    positionUserUpdListener.processStockUpdateMessage("", JSON.toJSONString(stock))
                                    TimeUnit.MILLISECONDS.sleep(1)

                                    stock.isOpen == "1"
                                } else {
                                    logger.warn("Failed to process stock data: ${jsonObject.getString("Symbol")}")
                                    false
                                }
                            } catch (e: Exception) {
                                logger.error(e) { "Error processing stock data: ${item}" }
                                false
                            }
                        }, stockProcessingExecutor)

                        futures.add(future)
                    }

                    // 等待所有任务完成并检查是否有开市的市场
                    futures.forEach { future ->
                        try {
                            if (future.get()) {
                                hasOpenMarket = true
                            }
                        } catch (e: Exception) {
                            logger.error(e) { "Error waiting for stock processing task" }
                        }
                    }
                }
                page++
            }
            countryMarketStatus[countryId.toString()] = if (hasOpenMarket) "1" else "0"
        }
        checkAndRestartWebSocket(countryMarketStatus)
        stockService.loadStockPid2Redis()
        System.gc()
    }

    private fun parseStockFromJson(jsonObject: JSONObject): Stock {
        val stock = Stock()

        stock.pId = jsonObject.getLong("Id")
        stock.name = jsonObject.getString("Name")
        stock.symbol = jsonObject.getString("Symbol")
        stock.isCfd = jsonObject.getString("IsCFD")
        stock.high = jsonObject.getBigDecimal("High")
        stock.low = jsonObject.getBigDecimal("Low")
        stock.last = jsonObject.getBigDecimal("Last")
        stock.lastPairDecimal = jsonObject.getInteger("LastPairDecimal")
        stock.chg = jsonObject.getBigDecimal("Chg")
        stock.chgPct = jsonObject.getBigDecimal("ChgPct")
        stock.volume = jsonObject.getLong("Volume")
        stock.avgVolume = jsonObject.getLong("AvgVolume")
        stock.time = jsonObject.getLong("Time")
        stock.isOpen = jsonObject.getString("IsOpen")
        stock.url = jsonObject.getString("Url")
        stock.flag = jsonObject.getString("Flag")
        stock.countryNameTranslated = jsonObject.getString("CountryNameTranslated")
        stock.exchangeId = jsonObject.getString("ExchangeId")
        stock.performanceDay = jsonObject.getBigDecimal("PerformanceDay")
        stock.performanceWeek = jsonObject.getBigDecimal("PerformanceWeek")
        stock.performanceMonth = jsonObject.getBigDecimal("PerformanceMonth")
        stock.performanceYtd = jsonObject.getBigDecimal("PerformanceYtd")
        stock.performanceYear = jsonObject.getBigDecimal("PerformanceYear")
        stock.performance3year = jsonObject.getBigDecimal("Performance3Year")
        stock.technicalHour = jsonObject.getString("TechnicalHour")
        stock.technicalDay = jsonObject.getString("TechnicalDay")
        stock.technicalWeek = jsonObject.getString("TechnicalWeek")
        stock.technicalMonth = jsonObject.getString("TechnicalMonth")
        stock.fundamentalMarketCap = jsonObject.getLong("FundamentalMarketCap")
        stock.fundamentalRevenue = jsonObject.getString("FundamentalRevenue")
        stock.fundamentalRatio = jsonObject.getBigDecimal("FundamentalRatio")
        stock.fundamentalBeta = jsonObject.getBigDecimal("FundamentalBeta")
        stock.pairType = jsonObject.getString("PairType")

        return stock
    }

    private fun checkAndRestartWebSocket(countryMarketStatus: Map<String, String>) {
        try {
            var needRestart = false

            countryMarketStatus.forEach { (countryId, currentStatus) ->
                val redisKey = "market_status:$countryId"
                val previousStatus = redisTemplate.opsForValue().get(redisKey) as String?

                // 检查状态是否发生变化（包括从开市变为休市，或从休市变为开市）
                if (previousStatus != null && previousStatus != currentStatus) {
                    logger.info { "Market status changed for country $countryId: $previousStatus -> $currentStatus" }
                    needRestart = true
                }

                // 更新 Redis 中的状态，不设置过期时间
                redisTemplate.opsForValue().set(redisKey, currentStatus)
            }

            if (needRestart) {
                logger.info { "Market status changed, restarting WebSocket..." }
                wsStarter.restart()
            } else {
                logger.debug { "No market status changes detected, WebSocket remains unchanged" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error checking market status changes" }
        }
    }


}