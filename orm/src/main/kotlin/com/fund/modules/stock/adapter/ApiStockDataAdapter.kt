package com.fund.modules.stock.adapter

import com.fund.modules.stock.model.Stock
import java.math.BigDecimal

/**
 * API股票数据适配器
 * 用于处理从API获取的股票数据
 */
class ApiStockDataAdapter(
    private val apiData: Any, // 可以是StockData或其他API数据格式
    private val code: String?
) : StockDataAdapter {
    
    private var pId: Long? = null
    
    override fun convertToStock(): Stock {
        return Stock().apply {
            // 基本信息
            this.symbol = code
            this.name = getName()
            
            // 价格信息
            this.last = getPrice()
            this.high = getHigh()
            this.low = getLow()
            
            // 成交量
            this.volume = getVolume()
            
            // 涨跌幅信息
            this.chg = getChange()
            this.chgPct = getChangePercent()
            
            // 时间戳
            this.time = getTimestamp()
            
            // 市场信息
            this.flag = getMarket()
            
            // 默认值
            this.isOpen = "1"
            this.sourceType = getDataSource().name
            
            // 使用设置的pId
            this.pId = this@ApiStockDataAdapter.pId
        }
    }
    
    override fun getSymbol(): String? = code
    
    override fun getName(): String? {
        return when (apiData) {
            is Map<*, *> -> apiData["name"] as? String ?: apiData["Name"] as? String
            else -> extractFieldByReflection("name") ?: extractFieldByReflection("Name")
        }
    }
    
    override fun getPrice(): BigDecimal? {
        return when (apiData) {
            is Map<*, *> -> {
                val price = apiData["price"] ?: apiData["Last"]
                when (price) {
                    is String -> price.toBigDecimalOrNull()
                    is Number -> BigDecimal.valueOf(price.toDouble())
                    else -> null
                }
            }
            else -> extractBigDecimalField("price") ?: extractBigDecimalField("Last")
        }
    }
    
    override fun getHigh(): BigDecimal? {
        return when (apiData) {
            is Map<*, *> -> {
                val high = apiData["high"] ?: apiData["High"]
                when (high) {
                    is String -> high.toBigDecimalOrNull()
                    is Number -> BigDecimal.valueOf(high.toDouble())
                    else -> null
                }
            }
            else -> extractBigDecimalField("high") ?: extractBigDecimalField("High")
        }
    }
    
    override fun getLow(): BigDecimal? {
        return when (apiData) {
            is Map<*, *> -> {
                val low = apiData["low"] ?: apiData["Low"]
                when (low) {
                    is String -> low.toBigDecimalOrNull()
                    is Number -> BigDecimal.valueOf(low.toDouble())
                    else -> null
                }
            }
            else -> extractBigDecimalField("low") ?: extractBigDecimalField("Low")
        }
    }
    
    override fun getVolume(): Long? {
        return when (apiData) {
            is Map<*, *> -> {
                val volume = apiData["volume"] ?: apiData["Volume"]
                when (volume) {
                    is String -> volume.toLongOrNull()
                    is Number -> volume.toLong()
                    else -> null
                }
            }
            else -> extractLongField("volume") ?: extractLongField("Volume")
        }
    }
    
    override fun getTimestamp(): Long? {
        return when (apiData) {
            is Map<*, *> -> {
                val tick = apiData["tick"] ?: apiData["timestamp"] ?: apiData["Time"]
                when (tick) {
                    is Number -> tick.toLong()
                    is String -> tick.toLongOrNull()
                    else -> null
                }
            }
            else -> extractLongField("tick") ?: extractLongField("timestamp") ?: extractLongField("Time")
        }
    }
    
    override fun getMarket(): String? {
        return when (apiData) {
            is Map<*, *> -> apiData["market"] as? String ?: apiData["Market"] as? String ?: apiData["Flag"] as? String
            else -> extractFieldByReflection("market") ?: extractFieldByReflection("Market") ?: extractFieldByReflection("Flag")
        }
    }
    
    override fun getDataSource(): DataSourceType = DataSourceType.API
    
    override fun setPId(pId: Long?): StockDataAdapter {
        this.pId = pId
        return this
    }
    
    override fun getPId(): Long? = pId
    
    /**
     * 获取涨跌额
     */
    private fun getChange(): BigDecimal? {
        return when (apiData) {
            is Map<*, *> -> {
                val change = apiData["change"]
                when (change) {
                    is String -> change.toBigDecimalOrNull()
                    is Number -> BigDecimal.valueOf(change.toDouble())
                    else -> null
                }
            }
            else -> extractBigDecimalField("change")
        }
    }
    
    /**
     * 获取涨跌幅
     */
    private fun getChangePercent(): BigDecimal? {
        return when (apiData) {
            is Map<*, *> -> {
                val up = apiData["up"]
                when (up) {
                    is String -> up.toBigDecimalOrNull()
                    is Number -> BigDecimal.valueOf(up.toDouble())
                    else -> null
                }
            }
            else -> extractBigDecimalField("up")
        }
    }
    
    /**
     * 通过反射提取字段值
     */
    private fun extractFieldByReflection(fieldName: String): String? {
        return try {
            val field = apiData::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            field.get(apiData) as? String
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 提取BigDecimal字段
     */
    private fun extractBigDecimalField(fieldName: String): BigDecimal? {
        return try {
            val field = apiData::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            val value = field.get(apiData)
            when (value) {
                is String -> value.toBigDecimalOrNull()
                is Number -> BigDecimal.valueOf(value.toDouble())
                is BigDecimal -> value
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 提取Long字段
     */
    private fun extractLongField(fieldName: String): Long? {
        return try {
            val field = apiData::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            val value = field.get(apiData)
            when (value) {
                is String -> value.toLongOrNull()
                is Number -> value.toLong()
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

}
