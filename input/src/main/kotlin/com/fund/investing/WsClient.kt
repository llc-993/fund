package com.fund.investing

import mu.KotlinLogging
import okhttp3.*
import okio.ByteString
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import com.fund.modules.stock.model.Stock
import com.alibaba.fastjson2.JSON
import com.fund.common.RedisKeys.STOCK_KEY
import com.fund.common.RedisKeys.STOCK_MESSAGE_QUEUE
import org.redisson.api.RedissonClient

@Component
class WsClient(
    private val redissonClient: RedissonClient
) {
    private val log = KotlinLogging.logger {}

    private val client: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(20, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var subscribedStocks: List<Stock> = emptyList()
    private val isRunning = AtomicBoolean(false)
    private var reconnectTask: Thread? = null

    fun initWs(stocks: List<Stock>): WebSocket? {
        this.subscribedStocks = stocks
        this.isRunning.set(true)
        return connect()
    }

    private fun connect(): WebSocket? {
        if (!isRunning.get()) return null

        val request = Request.Builder()
            .url(WS_URL)
            .header("Origin", "https://cn.investing.com")
            .header("User-Agent", USER_AGENT)
            .header("Accept-Language", "en-US,zh;q=0.9")
            .build()

        val newWebSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                log.info { "WebSocket opened: code=${response.code}" }
                // 连接成功后发送订阅消息
                sendSubscriptionMessage(webSocket)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                //  log.info { "返回的数据是: $text" }

                // 解析返回的数据并更新Stock
                try {
                    parseAndUpdateStock(text)
                } catch (e: Exception) {
                    log.error(e) { "Failed to parse WebSocket message" }
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                log.info { "WebSocket message[bytes]: ${bytes.hex()}" }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                log.info { "WebSocket closing: code=$code, reason=$reason" }
                webSocket.close(code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                log.info { "WebSocket closed: code=$code, reason=$reason" }
                // 如果不是主动关闭，则尝试重连
                if (isRunning.get() && code != 1000) {
                    scheduleReconnect()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                log.warn(t) { "WebSocket failure: code=${response?.code}, message=${response?.message}" }
                // 失败时自动重连
                if (isRunning.get()) {
                    scheduleReconnect()
                }
            }
        })

        this.webSocket = newWebSocket
        return newWebSocket
    }

    private fun sendSubscriptionMessage(webSocket: WebSocket) {
        if (subscribedStocks.isEmpty()) {
            log.warn { "No stocks to subscribe" }
            return
        }

        try {
            val subscriptionMessage = buildSubscriptionMessage()
            // log.info { "Sending subscription message: $subscriptionMessage" }
            webSocket.send(subscriptionMessage)
        } catch (e: Exception) {
            log.error(e) { "Failed to send subscription message" }
        }
    }

    private fun buildSubscriptionMessage(): String {
        if (subscribedStocks.isEmpty()) {
            return ""
        }

        // 构建 pid- 格式的字符串，用 %% 连接
        val msgBuild = StringBuilder()
        for (stock in subscribedStocks) {
            stock.pId?.let { id ->
                msgBuild.append("pid-").append(id).append(":%%")
            }
        }

        if (msgBuild.isNotEmpty()) {
            // 去掉最后的 %% 分隔符
            val message = msgBuild.substring(0, msgBuild.length - 2)

            // 构建参数Map
            val paramMap = mapOf(
                "_event" to "bulk-subscribe",
                "tzID" to 8,
                "message" to message
            )

            // 转换为JSON字符串
            val msg = JSON.toJSONString(paramMap)

            // 包装成数组格式
            val list = listOf(msg)
            val jsonString = JSON.toJSONString(list)

            return jsonString
        }

        return ""
    }

    private fun parseAndUpdateStock(message: String) {
        try {
            // 解析外层数组格式: a["{...}"]
            if (message.length < 2) return
            val jsonArray = JSON.parseArray(message.substring(1))
            if (jsonArray.isEmpty()) return

            val innerJsonString = jsonArray.getString(0)
            val innerJson = JSON.parseObject(innerJsonString)

            // 获取message字段
            val stockMessage = innerJson.getString("message")
            if (stockMessage.isNullOrEmpty()) return

            // 解析pid-{id}::{data}格式
            val parts = stockMessage.split("::", limit = 2)
            if (parts.size != 2) return

            val pidPart = parts[0] // pid-1137571
            val dataPart = parts[1] // {"pid":"1137571","last":"12.625",...}

            // 提取股票ID
            val stockId = pidPart.removePrefix("pid-").toLongOrNull()
            if (stockId == null) return
            // TODO 需要根据pid查找对应的 stock.id
            // 解析股票数据
            val stockData = JSON.parseObject(dataPart)

            // 查找对应的Stock对象并更新
            val bucket = redissonClient.getBucket<String>(STOCK_KEY + stockId)

            if (bucket.isExists) {
                val stock = JSON.parseObject(bucket.get(), Stock::class.java)
                updateStockFromData(stock, stockData)
                bucket.set(JSON.toJSONString(stock))

                // 发送股票数据更新消息到 Redis 队列
                sendStockUpdateMessage(stock)
            } else {
                log.warn { "Stock with ID $stockId not found in subscribed list" }
                val stock = Stock()
                updateStockFromData(stock, stockData)
                bucket.set(JSON.toJSONString(stock))

                // 发送股票数据更新消息到 Redis 队列
                sendStockUpdateMessage(stock)
            }

        } catch (e: Exception) {
            log.error(e) { "Error parsing stock data: $message" }
        }
    }

    private fun updateStockFromData(stock: Stock, stockData: com.alibaba.fastjson2.JSONObject) {
        try {
            // 更新价格相关数据
            stockData.getString("last")?.let { stock.last = it.toBigDecimalOrNull() }
            stockData.getString("high")?.let { stock.high = it.toBigDecimalOrNull() }
            stockData.getString("low")?.let { stock.low = it.toBigDecimalOrNull() }
            stockData.getString("bid")?.let { /* 可以添加到Stock类中 */ }
            stockData.getString("ask")?.let { /* 可以添加到Stock类中 */ }

            // 更新变化数据
            stockData.getString("pc")?.let { stock.chg = it.toBigDecimalOrNull() }
            stockData.getString("pcp")?.let { stock.chgPct = it.removeSuffix("%").toBigDecimalOrNull() }

            // 更新成交量
            stockData.getString("turnover")?.let { stock.volume = it.toLongOrNull() }
            stockData.getLong("turnover_numeric")?.let { /* 可以添加到Stock类中 */ }

            // 更新时间
            stockData.getLong("timestamp")?.let { stock.time = it }

            // 更新其他字段
            stockData.getString("last_close")?.let { stock.last = it.toBigDecimalOrNull() }
            stockData.getString("time")?.let { /* 可以添加到Stock类中 */ }

            log.debug { "Stock ${stock.symbol} updated successfully" }

        } catch (e: Exception) {
            log.error(e) { "Error updating stock ${stock.symbol} with data" }
        }
    }

    private fun sendStockUpdateMessage(stock: Stock) {
        try {
            // 将消息发送到 Redis 队列
            val rTopic = redissonClient.getTopic(STOCK_MESSAGE_QUEUE)
            rTopic.publishAsync(JSON.toJSONString(stock))

            log.debug { "Stock update message sent for ${stock.symbol} (ID: ${stock.pId})" }
        } catch (e: Exception) {
            log.error(e) { "Failed to send stock update message for ${stock.symbol}" }
        }
    }

    private fun scheduleReconnect() {
        if (reconnectTask?.isAlive == true) return

        reconnectTask = Thread {
            try {
                log.info { "Scheduling reconnect in 3 seconds..." }
                Thread.sleep(3_000) // 等待3秒后重连
                if (isRunning.get()) {
                    log.info { "Attempting to reconnect WebSocket..." }
                    connect()
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                log.info { "Reconnect task interrupted" }
            }
        }.apply {
            isDaemon = true
            start()
        }
    }

    fun close() {
        isRunning.set(false)
        webSocket?.close(1000, "client closing")
        webSocket = null

        reconnectTask?.interrupt()
        reconnectTask = null
    }

    companion object {
        private const val WS_URL = "wss://streaming.forexpros.com/echo/422/kzw4r2wc/websocket"
        private const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 2025-01-15 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"
    }
}