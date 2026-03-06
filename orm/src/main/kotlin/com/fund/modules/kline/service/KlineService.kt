package com.fund.modules.kline.service

import com.fund.modules.kline.model.Kline
import com.fund.modules.stock.model.Stock

/**
 * K线数据服务接口
 */
interface KlineService {
    
    /**
     * 保存K线数据列表（存在则更新，不存在则新增）
     */
    fun saveKlines(klines: List<Kline>)
    
    /**
     * 根据股票代码和市场获取K线数据
     */
    fun getKlinesBySymbol(symbol: String, market: String, interval: String, limit: Int = 100): List<Kline>

    /**
     * 根据用户调控配置获取K线数据（调整价格后返回）
     */
    fun getKlinesBySymbolForUser(symbol: String, market: String, interval: String, limit: Int = 100, userId: Long?): List<Kline>
    
    /**
     * 根据时间范围获取K线数据
     */
    fun getKlinesByTimeRange(
        symbol: String, 
        market: String, 
        interval: String, 
        startTime: Long, 
        endTime: Long
    ): List<Kline>

    /**
     * 根据用户调控配置和时间范围获取K线数据
     */
    fun getKlinesByTimeRangeForUser(
        symbol: String,
        market: String,
        interval: String,
        startTime: Long,
        endTime: Long,
        userId: Long?
    ): List<Kline>
    
    /**
     * 删除过期的K线数据
     */
    fun deleteExpiredKlines(beforeTimestamp: Long)
    
    /**
     * 处理K线消息
     */
    fun processKlineMessage(stock: Stock)
}