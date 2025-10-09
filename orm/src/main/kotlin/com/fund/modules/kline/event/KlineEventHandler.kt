package com.fund.modules.kline.event

import com.fund.modules.kline.service.KlineService
import com.lmax.disruptor.EventHandler
import mu.KotlinLogging

/**
 * K线事件处理器
 */
class KlineEventHandler(
    private val klineService: KlineService
) : EventHandler<KlineEvent> {
    
    private val logger = KotlinLogging.logger {}
    
    override fun onEvent(event: KlineEvent, sequence: Long, endOfBatch: Boolean) {
        try {
            val stock = event.stock
            if (stock != null) {
                // 委托给 KlineService 处理
                klineService.processKlineMessage(stock)
            }
        } catch (e: Exception) {
            logger.error(e) { "处理K线事件失败: sequence=$sequence" }
        }
    }
}

