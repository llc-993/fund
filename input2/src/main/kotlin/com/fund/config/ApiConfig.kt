package com.fund.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * API配置类
 * 用于管理各种API的配置信息
 */
@Component
@ConfigurationProperties(prefix = "api")
data class ApiConfig(
    /**
     * 股票API配置
     */
    var stock: StockApiConfig = StockApiConfig()
)

/**
 * 股票API配置
 */
data class StockApiConfig(
    /**
     * 授权令牌
     */
    var authorization: String = "",
    
    /**
     * API基础URL
     */
    var baseUrl: String = "http://hk.psbangu.cn:8001",
    
    /**
     * 价格接口路径
     */
    var pricesPath: String = "/api/prices",

    var miniListPath: String = "/api/mini_list",

    var ipoPath: String = "/api/ipo_listing"
) {
    /**
     * 获取完整的价格接口URL
     */
    fun getPricesUrl(): String {
        return "$baseUrl$pricesPath"
    }
    
    /**
     * 获取带市场参数的价格接口URL
     */
    fun getPricesUrlWithMarket(market: String): String {
        return "$baseUrl$pricesPath?market=$market"
    }

    fun getMiniListUrl(market: String, symbol: String, interval: String, page: String): String {
        return "$baseUrl$miniListPath?market=$market&symbol=${symbol}&interval=${interval}&page=${page}"
    }
    fun getIpoDataUrl(country: String): String {
        return "$baseUrl$ipoPath?country=$country"
    }
}
