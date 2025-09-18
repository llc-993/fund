package com.fund.modules.stock.adapter

import com.fund.modules.stock.model.Stock
import mu.KotlinLogging

/**
 * 股票数据适配器工厂
 * 根据不同的数据类型创建相应的适配器
 */
object StockDataAdapterFactory {
    
    private val logger = KotlinLogging.logger {}
    
    /**
     * 创建API数据适配器
     */
    fun createApiAdapter(apiData: Any, code: String?): StockDataAdapter {
        return ApiStockDataAdapter(apiData, code)
    }
    
    /**
     * 创建数据库数据适配器
     */
    fun createDatabaseAdapter(stock: Stock): StockDataAdapter {
        return DatabaseStockDataAdapter(stock)
    }
    
    
    /**
     * 自动识别数据类型并创建适配器
     */
    fun createAdapter(data: Any, additionalInfo: Map<String, Any> = emptyMap()): StockDataAdapter? {
        return try {
            when (data) {
                is Stock -> createDatabaseAdapter(data)
                is Map<*, *> -> {
                    val code = additionalInfo["code"] as? String ?: data["symbol"] as? String
                    createApiAdapter(data, code)
                }
                else -> {
                    // 尝试通过反射识别数据类型
                    val className = data::class.java.simpleName
                    when {
                        className.contains("Stock", ignoreCase = true) -> {
                            if (data is Stock) {
                                createDatabaseAdapter(data)
                            } else {
                                val code = additionalInfo["code"] as? String
                                createApiAdapter(data, code)
                            }
                        }
                        else -> {
                            val code = additionalInfo["code"] as? String
                            createApiAdapter(data, code)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error creating adapter for data type: ${data::class.java}" }
            null
        }
    }
    
    
    /**
     * 验证并创建适配器
     */
    fun createValidAdapter(data: Any, additionalInfo: Map<String, Any> = emptyMap()): StockDataAdapter? {
        val adapter = createAdapter(data, additionalInfo)
        return if (adapter?.isValid() == true) {
            adapter
        } else {
            logger.warn("Created adapter is not valid for data: $data")
            null
        }
    }
}
