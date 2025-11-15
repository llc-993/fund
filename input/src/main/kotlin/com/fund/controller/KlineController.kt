package com.fund.controller

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.fund.investing.InvestingClient
import com.fund.modules.kline.Kline
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/kline")
class KlineController(
    private var investingClient: InvestingClient
) {

    companion object {
        private const val DATA_SOURCE = "investing"
        private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        private val ZONE_ID: ZoneId = ZoneId.systemDefault()
        var URI: String =
            "https://tvc4.investing.com/f7227f007c2f7d041a94f074efa5a021/1763183780/6/6/28/history?symbol=%s&resolution=%s&from=%s&to=%s"
    }

    @GetMapping("")
    fun getKline(symbol: String, resolution: String, from: String, to: String): List<Kline> {
        val url = String.format(URI, symbol, resolution, from, to)
        val body = investingClient.loadData(url)
        val json = JSON.parseObject(body)

        if (!"ok".equals(json.getString("s"), ignoreCase = true)) {
            return emptyList()
        }

        val timeArray = json.getJSONArray("t") ?: return emptyList()
        val closeArray = json.getJSONArray("c") ?: return emptyList()
        val openArray = json.getJSONArray("o") ?: return emptyList()
        val highArray = json.getJSONArray("h") ?: return emptyList()
        val lowArray = json.getJSONArray("l") ?: return emptyList()
        val volumeArray = json.getJSONArray("v")
        val countArray = json.getJSONArray("vo")

        val length = listOf(
            timeArray.size,
            closeArray.size,
            openArray.size,
            highArray.size,
            lowArray.size
        ).minOrNull() ?: 0

        if (length == 0) {
            return emptyList()
        }

        val result = ArrayList<Kline>(length)
        for (index in 0 until length) {
            val kline = Kline().apply {
                this.symbol = symbol
                this.period = resolution
                this.dataSource = DATA_SOURCE
                this.source = 1
                this.dateTimeStr = formatEpoch(timeArray.getLongValue(index))
                this.open = openArray.getBigDecimal(index)
                this.close = closeArray.getBigDecimal(index)
                this.high = highArray.getBigDecimal(index)
                this.low = lowArray.getBigDecimal(index)
                this.vol = volumeArray?.getBigDecimal(index)
                this.volume = this.vol
                this.count = countArray?.getIntValue(index)
            }
            result.add(kline)
        }
        return result
    }

    private fun JSONArray.getBigDecimal(index: Int): BigDecimal {
        return when (val value = this.get(index)) {
            is BigDecimal -> value
            is Number -> BigDecimal.valueOf(value.toDouble())
            is String -> runCatching { BigDecimal(value) }.getOrElse { BigDecimal.ZERO }
            else -> BigDecimal.ZERO
        }
    }

    private fun formatEpoch(epochSecond: Long): String {
        return Instant.ofEpochSecond(epochSecond)
            .atZone(ZONE_ID)
            .format(DATE_TIME_FORMATTER)
    }
}