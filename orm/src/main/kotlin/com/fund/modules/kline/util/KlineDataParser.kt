package com.fund.modules.kline.util

import com.fund.modules.kline.model.Kline
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * K线数据解析器
 * 解析API返回的K线数据字符串格式
 */
@Component
class KlineDataParser {

    private val logger = KotlinLogging.logger {}

    /**
     * 解析K线数据字符串
     * 格式: "1757930460,200.07,200.07,200.07,200.07,200.07,1.0;..."
     * 每行格式: timestamp,open,high,low,close,volume
     */
    fun parseKlineData(
        dataString: String,
        symbol: String,
        market: String,
        interval: String
    ): List<Kline> {
        val klines = mutableListOf<Kline>()
        
        try {
            if (dataString.isBlank()) {
                return klines
            }
            
            // 按分号分割每行数据
            val lines = dataString.split(";").filter { it.isNotBlank() }
            
            for (line in lines) {
                try {
                    val kline = parseKlineLine(line.trim(), symbol, market, interval)
                    if (kline != null) {
                        klines.add(kline)
                    }
                } catch (e: Exception) {
                    logger.warn(e) { "Failed to parse kline line: $line" }
                }
            }
            
            logger.info("Parsed ${klines.size} kline records for $symbol-$interval")
            
        } catch (e: Exception) {
            logger.error(e) { "Error parsing kline data for $symbol-$interval" }
        }
        
        return klines
    }
    
    /**
     * 解析单行K线数据
     * 格式: "timestamp,open,high,low,close,volume"
     */
    private fun parseKlineLine(
        line: String,
        symbol: String,
        market: String,
        interval: String
    ): Kline? {
        try {
            val parts = line.split(",")
            if (parts.size < 6) {
                logger.warn("Invalid kline line format: $line")
                return null
            }
            
            val timestamp = parts[0].toLongOrNull() ?: return null
            val open = parts[1].toBigDecimalOrNull() ?: return null
            val high = parts[2].toBigDecimalOrNull() ?: return null
            val low = parts[3].toBigDecimalOrNull() ?: return null
            val close = parts[4].toBigDecimalOrNull() ?: return null
            val volume = parts[5].toBigDecimalOrNull() ?: return null
            
            return Kline(
                symbol = symbol,
                market = market,
                interval = interval,
                timestamp = timestamp,
                open = open,
                high = high,
                low = low,
                close = close,
                volume = volume
            )
            
        } catch (e: Exception) {
            logger.warn(e) { "Failed to parse kline line: $line" }
            return null
        }
    }
}