package com.fund.job

import cn.hutool.core.date.DateUtil
import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.enums.KlineEnum
import com.fund.investing.InvestingClient
import com.fund.modules.kline.model.Kline
import com.fund.modules.kline.service.KlineService
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.service.StockService
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * k线同步
 */
@Component
class KlineJob(
    private val stockService: StockService,
    private var investingClient: InvestingClient,
    private val klineService: KlineService
) {

    private val log = KotlinLogging.logger {}

    companion object {
        private const val DATA_SOURCE = "investing"
        private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        private val ZONE_ID: ZoneId = ZoneId.systemDefault()
        var URI: String =
            "https://tvc4.investing.com/f7227f007c2f7d041a94f074efa5a021/1763183780/6/6/28/history?symbol=%s&resolution=%s&from=%s&to=%s"

        var CHART_URI = "https://api.investing.com/api/financialdata/%s/historical/chart/?interval=%s&pointscount=%s"
    }

    @Scheduled(cron = "0 */1 * * * ?")
    fun syncKline() {
        val list = stockService.list(
            KtQueryWrapper(Stock())
                .eq(Stock::sourceType, "investing")
        )
        val end = DateUtil.current() / 1000
        val start = DateUtil.offsetMinute(Date(), -2000)

        for (stock in list) {
            try {
                for (klineEnum in KlineEnum.values()) {
                    val interval = klineEnum.investingInterval
                    val url = String.format(URI, stock.pId, interval, start.time / 1000, end)
                    log.info { "url: $url" }
                    val str = investingClient.loadData(url)

                    // log.info { "得到的数据是: $str " }

                    val json = JSON.parseObject(str)

                    if (!"ok".equals(json.getString("s"), ignoreCase = true)) {
                        return
                    }

                    val timeArray = json.getJSONArray("t")
                    val closeArray = json.getJSONArray("c")
                    val openArray = json.getJSONArray("o")
                    val highArray = json.getJSONArray("h")
                    val lowArray = json.getJSONArray("l")
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
                        return
                    }

                    val klines: MutableList<Kline> = ArrayList()
                    for (index in 0 until length) {
                        val kline = Kline(
                            id = timeArray.getLongValue(index).toString(),
                            symbol = stock.symbol!!,
                            market = stock.pairType ?: "",
                            interval = interval,
                            timestamp = timeArray.getLongValue(index),
                            open = openArray.getBigDecimal(index).setScale(3, RoundingMode.HALF_DOWN),
                            close = closeArray.getBigDecimal(index).setScale(3, RoundingMode.HALF_DOWN),
                            high = highArray.getBigDecimal(index).setScale(3, RoundingMode.HALF_DOWN),
                            low = lowArray.getBigDecimal(index).setScale(3, RoundingMode.HALF_DOWN),
                            volume = volumeArray?.getBigDecimal(index)!!.setScale(3, RoundingMode.HALF_DOWN),
                            createTime = System.currentTimeMillis()
                        )
                        klines.add(kline)
                    }
                    klineService.saveKlines(klines)
                }
            } catch (e: Exception) {
                log.error("定时任务异常", e)
            }
        }

    }

    private fun formatEpoch(epochSecond: Long): String {
        return Instant.ofEpochSecond(epochSecond)
            .atZone(ZONE_ID)
            .format(DATE_TIME_FORMATTER)
    }


}
