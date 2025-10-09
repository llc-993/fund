package com.fund.modules.kline.event

import com.fund.modules.stock.model.Stock

/**
 * K线事件 - 用于 Disruptor 传递 Stock 数据
 */
class KlineEvent {
    
    var stock: Stock? = null
    
    /**
     * 清空事件数据
     */
    fun clear() {
        stock = null
    }
    
    /**
     * 设置事件数据
     */
    fun setData(stock: Stock) {
        this.stock = stock
    }
}

