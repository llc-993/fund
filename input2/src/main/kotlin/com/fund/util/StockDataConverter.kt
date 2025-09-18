package com.fund.util

import com.fund.enetity.StockData
import com.fund.modules.stock.model.StockDataModel
import org.springframework.stereotype.Component

/**
 * StockData转换器
 * 将input2模块的StockData转换为通用的StockDataModel
 */
@Component
class StockDataConverter {
    
    /**
     * 将input2的StockData转换为通用的StockDataModel
     * @param stockData input2的StockData对象
     * @return 通用的StockDataModel对象
     */
    fun convertToStockDataModel(stockData: StockData): StockDataModel {
        return StockDataModel().apply {
            // 基本信息
            this.code = stockData.code
            this.tick = stockData.tick
            this.name = stockData.name
            this.market = stockData.market
            
            // 价格信息
            this.price = stockData.price
            this.open = stockData.open
            this.high = stockData.high
            this.low = stockData.low
            this.close = stockData.close
            this.ys = stockData.ys
            
            // 成交量信息
            this.volume = stockData.volume
            this.nv = stockData.nv
            this.vwap = stockData.vwap
            this.amount = stockData.amount
            
            // 涨跌幅信息
            this.change = stockData.change
            this.up = stockData.up
            this.amplitude = stockData.amplitude
            
            // 买卖盘信息
            this.b1 = stockData.b1
            this.b1v = stockData.b1v
            this.s1 = stockData.s1
            this.s1v = stockData.s1v
            
            // 其他信息
            this.position = stockData.position
            this.varieties = stockData.varieties
            this.t = stockData.t
            this.contract = stockData.contract
            this.products = stockData.products
            this.mrta = stockData.mrta
            this.zdt = stockData.zdt
            this.dealTransaction = stockData.dealTransaction
        }
    }
    
    /**
     * 批量转换StockData列表
     * @param stockDataList input2的StockData列表
     * @return 通用的StockDataModel列表
     */
    fun convertToStockDataModelList(stockDataList: List<StockData>): List<StockDataModel> {
        return stockDataList.map { convertToStockDataModel(it) }
    }
}
