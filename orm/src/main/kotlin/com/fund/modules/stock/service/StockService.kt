package com.fund.modules.stock.service;

import com.fund.modules.stock.model.Stock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.stock.QueryStockRequest

/**
 * <p>
 * 股票行情数据表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-08-12
 */
interface StockService : IService<Stock> {

    fun upsertById(stock: Stock): Boolean

    fun list(req: QueryStockRequest): R<Any>

    fun countryList(): R<Any>

    fun getStockById(stockId: Long): Stock
}
