package com.fund.ws

import com.alibaba.fastjson2.JSON
import com.fund.common.RedisKeys.STOCK_MESSAGE_QUEUE
import com.fund.enetity.JsonBean
import com.fund.enetity.StockData
import com.fund.modules.stock.adapter.StockDataProcessor
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.service.StockDataRedisService
import com.fund.util.StockDataConverter
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@Component
class WsClient(
    private val redissonClient: RedissonClient,
    private val stockDataProcessor: StockDataProcessor
) {

    @Autowired
    private lateinit var stockService: StockService
    
    @Autowired
    private lateinit var stockDataRedisService: StockDataRedisService
    
    @Autowired
    private lateinit var stockDataConverter: StockDataConverter

    private val log = KotlinLogging.logger {}

    private val client: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(20, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)
        .build()

    fun init() {
        val request = Request.Builder()
            .url("ws://hk.psbangu.cn:9004/connect/json/demolins56")
            .header("Accept-Language", "en-US,zh;q=0.9")
            .build()

        client.newWebSocket(request, object : WebSocketListener() {
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                log.info("Closed $reason")

            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                log.info("Closed $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                log.info("Failure $t")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                log.info("WebSocket Message: $text")
                
                try {
                    // 解析WebSocket消息
                    val jsonBean = JSON.parseObject(text, JsonBean::class.java)
                    
                    if (jsonBean.state == 1 && jsonBean.msg != null) {
                        // 将msg字段转换为StockData对象
                        val stockData = JSON.parseObject(JSON.toJSONString(jsonBean.msg), StockData::class.java)

                        // 使用适配器处理数据
                        val stock = stockDataProcessor.processInputStockData(stockData, jsonBean.code, null)
                        
                        if (stock != null) {
                            // 构建买卖盘深度数据
                            val askDepth = mutableMapOf<String, Any>()
                            val bidDepth = mutableMapOf<String, Any>()

                            // 处理深度数据
                            stockData?.let { data ->
                                askDepth.put(data.s1!!, data.s1v!!)
                                bidDepth.put(data.s1!!, data.s1v!!)
                            }

                            // 设置深度数据到Stock对象
                            stock.askDepth = askDepth
                            stock.bidDepth = bidDepth

                            // 保存或更新股票数据
                            val result = stockService.upsertById(stock)
                            if (result) {

                                // 保存StockData到Redis
                                if (stock.id != null && stockData != null) {
                                    val stockDataModel = stockDataConverter.convertToStockDataModel(stockData)
                                    stockDataRedisService.saveStockData(stockDataModel, stock.id!!)
                                }
                            }
                            // 将消息发送到 Redis 队列
                            val rTopic = redissonClient.getTopic(STOCK_MESSAGE_QUEUE)
                            rTopic.publishAsync(JSON.toJSONString(stock))
                        }
                    }
                } catch (e: Exception) {
                    log.error(e) { "Error processing WebSocket message: $text" }
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                log.info("Message1 $bytes")
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send("/submkt/NSE")
                webSocket.send("/heartbeat/PING")
            }
        })

    }
    


}