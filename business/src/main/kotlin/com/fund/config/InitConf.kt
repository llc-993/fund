package com.fund.config

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.model.UserPosition
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.service.UserPositionService
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component


@Component
class InitConf(
    private val userPositionService: UserPositionService,
    private val stockService: StockService
): InitializingBean {


    // 加载持仓到redis中
    override fun afterPropertiesSet() {
        this.loadUserPosition2Redis()
    }


    fun loadUserPosition2Redis(){
        val list = userPositionService.list(
            KtQueryWrapper(UserPosition())
                .eq(UserPosition::status, 1)
        )
        val stocks = stockService.list()

        for (position in list) {
            var stock: Stock? = null
            
            // 如果 stockGid 不为空，从 stocks 列表中查找
            if (position.stockGid != null && position.stockGid!!.isNotBlank()) {
                stock = stocks.firstOrNull { stockItem -> 
                    stockItem.id?.toString() == position.stockGid 
                }
            }
            
            // 如果 stockGid 为空或未找到，则通过数据库查询
            if (stock == null) {
                stock = stockService.getOne(KtQueryWrapper(Stock())
                    .eq(Stock::symbol, position.stockCode)
                    .eq(Stock::flag, position.stockType)
                    .eq(Stock::name, position.stockName)
                    .last(" limit 1 ")
                )
            }

            // 如果找到了 stock，则更新缓存
            if (stock != null) {
                userPositionService.updatePositionCache(position, stock)
            }
        }
    }

}