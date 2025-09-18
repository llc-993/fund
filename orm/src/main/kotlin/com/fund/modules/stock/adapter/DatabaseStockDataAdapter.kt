package com.fund.modules.stock.adapter

import com.fund.modules.stock.model.Stock
import java.math.BigDecimal

/**
 * 数据库股票数据适配器
 * 用于处理从数据库获取的股票数据
 */
class DatabaseStockDataAdapter(
    private val stock: Stock
) : StockDataAdapter {
    
    override fun convertToStock(): Stock {
        // 对于数据库数据，直接返回原对象或创建副本
        return Stock().apply {
            this.id = stock.id
            this.symbol = stock.symbol
            this.name = stock.name
            this.isCfd = stock.isCfd
            this.high = stock.high
            this.low = stock.low
            this.last = stock.last
            this.lastPairDecimal = stock.lastPairDecimal
            this.chg = stock.chg
            this.chgPct = stock.chgPct
            this.volume = stock.volume
            this.avgVolume = stock.avgVolume
            this.time = stock.time
            this.isOpen = stock.isOpen
            this.url = stock.url
            this.flag = stock.flag
            this.countryNameTranslated = stock.countryNameTranslated
            this.exchangeId = stock.exchangeId
            this.performanceDay = stock.performanceDay
            this.performanceWeek = stock.performanceWeek
            this.performanceMonth = stock.performanceMonth
            this.performanceYtd = stock.performanceYtd
            this.performanceYear = stock.performanceYear
            this.performance3year = stock.performance3year
            this.technicalHour = stock.technicalHour
            this.technicalDay = stock.technicalDay
            this.technicalWeek = stock.technicalWeek
            this.technicalMonth = stock.technicalMonth
            this.fundamentalMarketCap = stock.fundamentalMarketCap
            this.fundamentalRevenue = stock.fundamentalRevenue
            this.fundamentalRatio = stock.fundamentalRatio
            this.fundamentalBeta = stock.fundamentalBeta
            this.pairType = stock.pairType
            this.pId = stock.pId
            this.sourceType = DataSourceType.DATABASE.name
        }
    }
    
    override fun getSymbol(): String? = stock.symbol
    
    override fun getName(): String? = stock.name
    
    override fun getPrice(): BigDecimal? = stock.last
    
    override fun getHigh(): BigDecimal? = stock.high
    
    override fun getLow(): BigDecimal? = stock.low
    
    override fun getVolume(): Long? = stock.volume
    
    override fun getTimestamp(): Long? = stock.time
    
    override fun getMarket(): String? = stock.flag
    
    override fun getDataSource(): DataSourceType = DataSourceType.DATABASE
    

    

    
    /**
     * 获取涨跌额
     */
    fun getChange(): BigDecimal? = stock.chg
    
    /**
     * 获取涨跌幅
     */
    fun getChangePercent(): BigDecimal? = stock.chgPct
    
    /**
     * 获取平均成交量
     */
    fun getAvgVolume(): Long? = stock.avgVolume
    
    /**
     * 获取数据库ID
     */
    fun getId(): Long? = stock.id
    

    
    /**
     * 是否为CFD
     */
    fun isCfd(): String? = stock.isCfd
    
    /**
     * 是否开市
     */
    fun isOpen(): String? = stock.isOpen
}
