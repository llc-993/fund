package com.fund.modules.kline.service

import com.fund.modules.kline.model.Kline

/**
 * K线数据服务接口
 */
interface KlineService {
    
    /**
     * 保存K线数据列表
     */
    fun saveKlines(klines: List<Kline>)
    
    /**
     * 根据股票代码和市场获取K线数据
     */
    fun getKlinesBySymbol(symbol: String, market: String, interval: String, limit: Int = 100): List<Kline>
    
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
     * 删除过期的K线数据
     */
    fun deleteExpiredKlines(beforeTimestamp: Long)
}