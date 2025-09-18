package com.fund.modules.stock.adapter

import com.fund.modules.stock.model.Stock
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * 股票数据处理器
 * 使用适配器模式统一处理不同来源的股票数据
 */
@Component
class StockDataProcessor {
    
    private val logger = KotlinLogging.logger {}
    
    /**
     * 处理单个股票数据
     */
    fun processStockData(data: Any, additionalInfo: Map<String, Any> = emptyMap()): Stock? {
        return try {
            val adapter = StockDataAdapterFactory.createValidAdapter(data, additionalInfo)
            
            if (adapter != null) {
                // 如果additionalInfo中包含pId，则设置到适配器中
                additionalInfo["pId"]?.let { pId ->
                    if (pId is Number) {
                        adapter.setPId(pId.toLong())
                    }
                }
                
                val stock = adapter.convertToStock()
                stock
            } else {
                logger.warn("Failed to create valid adapter for data: $data")
                null
            }
        } catch (e: Exception) {
            logger.error(e) { "Error processing stock data: $data" }
            null
        }
    }
    
    
    /**
     * 专门为input模块处理股票数据（支持pId设置）
     */
    fun processInputStockData(data: Any, code: String?, pId: Long? = null): Stock? {
        return try {
            val adapter = StockDataAdapterFactory.createApiAdapter(data, code)
            
            if (adapter.isValid()) {
                // 设置pId（如果提供）
                pId?.let { adapter.setPId(it) }
                
                val stock = adapter.convertToStock()
                stock
            } else {
                logger.warn("Invalid input stock data for code: $code")
                null
            }
        } catch (e: Exception) {
            logger.error(e) { "Error processing input stock data for code: $code" }
            null
        }
    }
    
}
