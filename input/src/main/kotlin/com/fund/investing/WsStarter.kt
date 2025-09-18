package com.fund.investing

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.service.StockService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.context.ApplicationContext
import mu.KotlinLogging
import okhttp3.WebSocket
import org.apache.commons.collections4.CollectionUtils

@Component
class WsStarter(
    private val stockService: StockService,
    private val applicationContext: ApplicationContext,
    private val wsClient:WsClient
) {
    private val log = KotlinLogging.logger {}
    
    // 存储所有创建的 WsClient 实例
    private val wsClients = mutableListOf<WebSocket?>()

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        start()
    }

    fun start() {
        log.info { "WsStarter starting..." }
        try {
            val ktQueryWrapper = KtQueryWrapper(Stock())
                .select(Stock::pId)
                .eq(Stock::isOpen, 1)

            val count = stockService.count(ktQueryWrapper)

            val pageSize = 1000L

            var pageMax = count / pageSize
            pageMax = pageMax + 1L
            
            log.info { "Processing $count stocks in $pageMax pages (page size: $pageSize)" }
            
            for (i in 1..pageMax) {
                val page: Page<Stock> = Page(i, pageSize)
                val pageStocks = stockService.list(page, ktQueryWrapper)
                
                if (CollectionUtils.isNotEmpty(pageStocks)) {
                    val initWs = wsClient.initWs(pageStocks)

                    // 将实例添加到列表中
                    wsClients.add(initWs)
                    
                    log.info { "WsClient instance created and initialized for page $i" }
                }
            }
            
            log.info { "WebSocket initialization completed. Created ${wsClients.size} WsClient instances" }
            
        } catch (e: Exception) {
            log.error(e) { "Failed to start WebSocket" }
        }
    }

    fun restart() {
        log.info { "WsStarter restarting..." }
        
        // 关闭所有现有的 WebSocket 连接
        wsClients.forEach { wsClient ->
            try {
                wsClient?.close(1000, "client closing")
            } catch (e: Exception) {
                log.error(e) { "Error closing WsClient instance" }
            }
        }
        
        // 清空列表
        wsClients.clear()
        // 重新启动
        start()
    }
}


