package com.fund.job

import cn.hutool.http.Header
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson.JSON
import com.fund.config.ApiConfig
import com.fund.constants.Constants
import com.fund.enetity.JsonBean
import com.fund.enetity.StockData
import com.fund.modules.stock.adapter.StockDataProcessor
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.service.StockDataRedisService
import com.fund.util.StockDataConverter
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

//@Component
class StockJob(
    private val apiConfig: ApiConfig,
){

    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var stockService: StockService

    @Autowired
    private lateinit var stockDataProcessor: StockDataProcessor
    
    @Autowired
    private lateinit var stockDataRedisService: StockDataRedisService
    
    @Autowired
    private lateinit var stockDataConverter: StockDataConverter


    @Scheduled(cron = "1 * * * * ?")
    fun loadStock() {
        logger.info("Loading stocks...")
        try {
            for (str in Constants.marketList) {
                val url = apiConfig.stock.getPricesUrlWithMarket(str)
                logger.info("Requesting stock prices from: $url")

                val httpRequest = HttpUtil.createGet(url)
                httpRequest.header(Header.AUTHORIZATION, apiConfig.stock.authorization)

                val body = httpRequest.execute().body()

                if (body == null) {
                    continue
                }

                // 先解析为JsonBean列表，然后手动转换Msg字段
                val stockList = JSON.parseArray(body, JsonBean::class.java)

                stockList?.let { list ->
                    for (stockBean in list) {
                        try {
                            // 手动将msg字段转换为StockData对象
                            val stockData = JSON.parseObject(JSON.toJSONString(stockBean.msg), StockData::class.java)

                            // 使用适配器处理数据
                            val stock = stockDataProcessor.processInputStockData(stockData, stockBean.code, null)
                            
                            if (stock != null) {
                                // 构建买卖盘深度数据
                                val askDepth = mutableMapOf<String, Any>()
                                val bidDepth = mutableMapOf<String, Any>()
                                
                                // 处理卖盘数据 (S1-S5)
                                stockData?.let { data ->
                                    askDepth.put(data.s1!!, data.s1v!!)
                                    bidDepth.put(data.s1!!, data.s1v!!)
                                }
                                
                                // 设置深度数据到Stock对象
                                stock.askDepth = askDepth
                                stock.bidDepth = bidDepth

                                // 调用upsertById方法保存或更新股票数据
                                val result = stockService.upsertById(stock)
                                if (result) {
                                    logger.debug("Successfully upserted stock: ${stock.symbol}")
                                    
                                    // 保存StockData到Redis（包含所有字段）
                                    if (stock.id != null && stockData != null) {
                                        val stockDataModel = stockDataConverter.convertToStockDataModel(stockData)
                                        stockDataRedisService.saveStockData(stockDataModel, stock.id!!)
                                        logger.debug("Successfully saved StockData to Redis: ${stock.symbol}")
                                    }
                                } else {
                                    logger.warn("Failed to upsert stock: ${stock.symbol}")
                                }
                            } else {
                                logger.warn("Failed to process stock data for code: ${stockBean.code}")
                            }
                        } catch (e: Exception) {
                            logger.error(e) { "Error processing stock: ${stockBean.code}" }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            logger.error(e) { "Error occurred while loading stocks." }
        }
    }




}