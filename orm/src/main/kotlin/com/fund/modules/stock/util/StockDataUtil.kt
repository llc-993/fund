package com.fund.modules.stock.util

import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.model.StockDataModel
import com.fund.modules.stock.service.StockDataRedisService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * StockData工具类
 * 提供Stock和StockData之间的转换和操作
 */
@Component
class StockDataUtil {
    
    private val logger = KotlinLogging.logger {}
    
    @Autowired
    private lateinit var stockDataRedisService: StockDataRedisService
    
    /**
     * 获取Stock的完整StockData信息
     * @param stock Stock对象
     * @return StockDataModel对象，如果不存在返回null
     */
    fun getFullStockData(stock: Stock): StockDataModel? {
        return if (stock.id != null) {
            stockDataRedisService.getStockData(stock.id!!)
        } else {
            null
        }
    }
    
    
    /**
     * 从StockDataModel中提取关键信息到Stock
     * @param stockData StockDataModel对象
     * @param stock 要更新的Stock对象
     */
    fun enrichStockFromStockData(stockData: StockDataModel, stock: Stock) {
        // 更新基本信息
        stock.name = stockData.name ?: stock.name
        stock.symbol = stockData.code ?: stock.symbol
        stock.flag = stockData.market ?: stock.flag
        
        // 更新价格信息
        stock.last = stockData.price ?: stock.last
        stock.high = stockData.high ?: stock.high
        stock.low = stockData.low ?: stock.low
        
        // 更新成交量
        stock.volume = stockData.volume ?: stock.volume
        
        // 更新涨跌幅信息
        stock.chg = stockData.change ?: stock.chg
        stock.chgPct = stockData.up ?: stock.chgPct
        
        // 更新时间戳
        stock.time = stockData.tick ?: stock.time
        
        logger.debug("Enriched stock from StockData: ${stock.symbol}")
    }
}
