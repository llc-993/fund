package com.fund.investing

import cn.hutool.core.util.RandomUtil
import mu.KotlinLogging
import okhttp3.*
import okio.ByteString
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import com.fund.modules.stock.model.Stock
import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.RedisKeys
import com.fund.common.RedisKeys.STOCK_KEY
import com.fund.common.RedisKeys.STOCK_MESSAGE_QUEUE
import com.fund.modules.stock.consumer.PositionUserUpdListener
import com.fund.modules.stock.service.StockService
import org.redisson.api.RedissonClient
import java.math.BigDecimal

@Component
class WsClient(
    private val redissonClient: RedissonClient,
    private val stockService: StockService,
    private val positionUserUpdListener: PositionUserUpdListener
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
                // log.info { "返回的数据是: $text" }

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
            //  log.info { "Sending subscription message: $subscriptionMessage" }
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
            if (stock.pId == null) continue
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
            val pid = pidPart.removePrefix("pid-").toLongOrNull()
            if (pid == null) return
            // TODO 需要根据pid查找对应的 stock.id
            // 解析股票数据
            val stockData = JSON.parseObject(dataPart)

            // 查找对应的Stock对象并更新
            // val bucket = redissonClient.getBucket<String>(STOCK_KEY + stockId)
            val map = redissonClient.getMap<Long, Long>(RedisKeys.STOCK_PID_KEY)
            if (map.containsKey(pid)) {
                val id = map.get(pid)
                val stock1 = stockService.getStockById(id!!)
                updateStockFromData(stock1, stockData)
                // upsertById 现在会自动保留 bidDepth 和 askDepth
                stockService.upsertById(stock1)
                // 发送股票数据更新消息到 Redis 队列
                sendStockUpdateMessage(stock1)
            } else {
                val stock = stockService.getOne(
                    KtQueryWrapper(Stock())
                        .eq(Stock::pId, pid)
                        .last(" limit 1 ")
                )
                map.put(pid, stock.id)
                updateStockFromData(stock, stockData)
                // upsertById 现在会自动保留 bidDepth 和 askDepth
                stockService.upsertById(stock)
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

            // 模拟金融股票的盘口算法：基于当前价格生成多档位买卖盘口
            val currentPrice = stock.last ?: stockData.getString("last")?.toBigDecimalOrNull()
            if (currentPrice != null) {
                generateMarketDepth(stock, currentPrice)
            } else {
                // 如果没有当前价格，使用原有的单个bid/ask逻辑
                stockData.getString("bid")?.let { bidPrice ->
                    val bidVolume = RandomUtil.randomInt(3, 30001)
                    if (stock.bidDepth.size > 6) {
                        while (stock.bidDepth.size > 6) {
                            stock.bidDepth.remove(stock.bidDepth.keys.first())
                        }
                    }
                    stock.bidDepth.put(bidPrice, bidVolume)
                }
                stockData.getString("ask")?.let { askPrice ->
                    val askVolume = RandomUtil.randomInt(3, 30000)
                    if (stock.askDepth.size > 6) {
                        while (stock.askDepth.size > 6) {
                            stock.askDepth.remove(stock.askDepth.keys.first())
                        }
                    }
                    stock.askDepth.put(askPrice, askVolume)
                }
            }

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
            stock.id = stock.id!!
            log.debug { "Stock ${stock.symbol} updated successfully" }

        } catch (e: Exception) {
            log.error(e) { "Error updating stock ${stock.symbol} with data" }
        }
    }

    /**
     * 模拟金融股票的盘口算法：基于当前价格生成多档位买卖盘口
     * 生成5档买盘和5档卖盘，每个档位有合理的价差和随机成交量
     * 
     * 算法特点：
     * 1. 根据价格大小动态调整价差（tick size）
     * 2. 买盘价格低于当前价格，卖盘价格高于当前价格
     * 3. 每个档位有随机因子微调价格，模拟真实市场
     * 4. 成交量随档位距离递减，第1档最大，第5档最小
     */
    private fun generateMarketDepth(stock: Stock, currentPrice: BigDecimal) {
        try {
            // 计算合理的价差（tick size）
            // 根据价格大小动态调整价差：价格越高，价差越大
            val (priceScale, scale) = when {
                currentPrice.compareTo(BigDecimal(1000)) >= 0 -> Pair(BigDecimal("0.1"), 1)   // 高价股：0.1
                currentPrice.compareTo(BigDecimal(100)) >= 0 -> Pair(BigDecimal("0.01"), 2)   // 中价股：0.01
                currentPrice.compareTo(BigDecimal(10)) >= 0 -> Pair(BigDecimal("0.001"), 3)    // 中低价股：0.001
                else -> Pair(BigDecimal("0.0001"), 4)  // 低价股：0.0001
            }
            
            // 生成5档买盘（bidDepth）：价格低于当前价格，从高到低
            val bidDepth = mutableMapOf<String, Any>()
            for (i in 1..5) {
                // 买盘价格 = 当前价格 - (i * 价差) + 随机微调（-30%到+30%的价差范围）
                val baseOffset = priceScale.multiply(BigDecimal(i))
                val randomFactor = BigDecimal(RandomUtil.randomDouble(-0.3, 0.3))
                val priceAdjustment = priceScale.multiply(randomFactor)
                val bidPrice = currentPrice.subtract(baseOffset)
                    .add(priceAdjustment)
                    .setScale(scale, java.math.RoundingMode.HALF_UP)
                    .coerceAtMost(currentPrice.subtract(priceScale)) // 确保低于当前价格
                
                // 随机成交量：越远离当前价格，成交量可能越小
                val baseVolume = RandomUtil.randomInt(100, 5000)
                val volumeFactor = (6 - i) / 5.0  // 第1档最大，第5档最小
                val bidVolume = (baseVolume * volumeFactor).toInt().coerceAtLeast(10)
                
                bidDepth[bidPrice.toString()] = bidVolume
            }
            
            // 生成5档卖盘（askDepth）：价格高于当前价格，从低到高
            val askDepth = mutableMapOf<String, Any>()
            for (i in 1..5) {
                // 卖盘价格 = 当前价格 + (i * 价差) + 随机微调（-30%到+30%的价差范围）
                val baseOffset = priceScale.multiply(BigDecimal(i))
                val randomFactor = BigDecimal(RandomUtil.randomDouble(-0.3, 0.3))
                val priceAdjustment = priceScale.multiply(randomFactor)
                val askPrice = currentPrice.add(baseOffset)
                    .add(priceAdjustment)
                    .setScale(scale, java.math.RoundingMode.HALF_UP)
                    .coerceAtLeast(currentPrice.add(priceScale)) // 确保高于当前价格
                
                // 随机成交量：越远离当前价格，成交量可能越小
                val baseVolume = RandomUtil.randomInt(100, 5000)
                val volumeFactor = (6 - i) / 5.0  // 第1档最大，第5档最小
                val askVolume = (baseVolume * volumeFactor).toInt().coerceAtLeast(10)
                
                askDepth[askPrice.toString()] = askVolume
            }
            
            // 更新盘口数据，保持最多6个档位（原有逻辑是保留最后7个，这里生成5个，所以直接替换）
            stock.bidDepth.clear()
            // 按价格从高到低排序买盘
            bidDepth.toList()
                .sortedByDescending { it.first.toBigDecimalOrNull() ?: BigDecimal.ZERO }
                .forEach { stock.bidDepth[it.first] = it.second }
            
            stock.askDepth.clear()
            // 按价格从低到高排序卖盘
            askDepth.toList()
                .sortedBy { it.first.toBigDecimalOrNull() ?: BigDecimal.ZERO }
                .forEach { stock.askDepth[it.first] = it.second }
            
        } catch (e: Exception) {
            log.error(e) { "Error generating market depth for stock ${stock.symbol}" }
        }
    }

    private fun sendStockUpdateMessage(stock: Stock) {
        try {
            // 将消息发送到 Redis 队列
            val rTopic = redissonClient.getTopic(STOCK_MESSAGE_QUEUE)

            // 验证 bidDepth 和 askDepth 是否已设置
            if (stock.bidDepth != null || stock.askDepth != null) {
                log.debug { "Stock ${stock.symbol} bidDepth: ${stock.bidDepth}, askDepth: ${stock.askDepth}" }
            }

            // 使用 WriteNulls 特性序列化所有字段（包括 null 值），确保推送完整的 Stock 对象
            // FastJSON2 默认支持 Map 的序列化，但需要确保 Map 不为 null
            val jsonString = JSON.toJSONString(stock, com.alibaba.fastjson2.JSONWriter.Feature.WriteNulls)

            // 检查序列化后的 JSON 是否包含 bidDepth 和 askDepth 字段
            if (stock.bidDepth != null && !jsonString.contains("\"bidDepth\"")) {
                log.warn { "bidDepth not found in serialized JSON for ${stock.symbol}. JSON length: ${jsonString.length}" }
            }
            if (stock.askDepth != null && !jsonString.contains("\"askDepth\"")) {
                log.warn { "askDepth not found in serialized JSON for ${stock.symbol}. JSON length: ${jsonString.length}" }
            }

            // 如果 bidDepth 或 askDepth 存在但未序列化，记录完整的 JSON 用于调试
            if ((stock.bidDepth != null || stock.askDepth != null) &&
                (!jsonString.contains("\"bidDepth\"") || !jsonString.contains("\"askDepth\""))
            ) {
                log.warn { "Serialized JSON (first 500 chars): ${jsonString.take(500)}" }
            }

            rTopic.publishAsync(jsonString)
            //  log.info { "接收到的数据是：$jsonString" }
            //  positionUserUpdListener.processStockUpdateMessage("", jsonString)
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