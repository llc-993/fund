package com.fund.modules.stock.adapter

import com.fund.modules.stock.model.Stock
import java.math.BigDecimal

/**
 * 股票数据适配器接口
 * 用于统一处理不同来源的股票数据
 */
interface StockDataAdapter {
    
    /**
     * 转换为Stock对象
     */
    fun convertToStock(): Stock
    
    /**
     * 获取股票代码
     */
    fun getSymbol(): String?
    
    /**
     * 获取股票名称
     */
    fun getName(): String?
    
    /**
     * 获取最新价格
     */
    fun getPrice(): BigDecimal?
    
    /**
     * 获取最高价
     */
    fun getHigh(): BigDecimal?
    
    /**
     * 获取最低价
     */
    fun getLow(): BigDecimal?
    
    /**
     * 获取成交量
     */
    fun getVolume(): Long?
    
    /**
     * 获取时间戳
     */
    fun getTimestamp(): Long?
    
    /**
     * 获取市场标识
     */
    fun getMarket(): String?
    
    /**
     * 获取数据源类型
     */
    fun getDataSource(): DataSourceType
    
    /**
     * 验证数据是否有效
     */
    fun isValid(): Boolean {
        return getSymbol()?.isNotBlank() == true && getPrice() != null
    }
    
    /**
     * 设置pId（仅用于特定场景，如input模块）
     */
    fun setPId(pId: Long?): StockDataAdapter {
        // 默认实现，子类可以重写
        return this
    }
    
    /**
     * 获取pId
     */
    fun getPId(): Long? = null
}

/**
 * 数据源类型枚举
 */
enum class DataSourceType {
    API,        // 来自API
    DATABASE,   // 来自数据库
    CACHE,      // 来自缓存
    WEBSOCKET,  // 来自WebSocket
    FILE        // 来自文件
}
