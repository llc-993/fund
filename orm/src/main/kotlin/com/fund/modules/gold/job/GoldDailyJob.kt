package com.fund.modules.gold.job

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.RedisKeys
import com.fund.modules.gold.mapper.AppGoldChannelMapper
import com.fund.modules.gold.mapper.AppGoldPositionMapper
import com.fund.modules.gold.mapper.AppGoldPriceQuoteMapper
import com.fund.modules.gold.model.AppGoldChannel
import com.fund.modules.gold.model.AppGoldPosition
import com.fund.modules.gold.model.AppGoldPriceQuote
import com.fund.modules.gold.service.GoldKlineMongoService
import com.fund.utils.RedisLockService
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

/**
 * 积存金每日任务：写入前收、重置当日浮动盈亏字段。
 * 使用分布式锁避免多实例重复执行。
 */
@Component
open class GoldDailyJob(
    private val channelMapper: AppGoldChannelMapper,
    private val quoteMapper: AppGoldPriceQuoteMapper,
    private val positionMapper: AppGoldPositionMapper,
    private val klineService: GoldKlineMongoService,
) {
    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "0 5 0 * * ?")
    open fun dailyReset() {
        try {
            RedisLockService.lock(RedisKeys.LOCK_GOLD_DAILY_JOB) {
                runDaily()
            }
        } catch (e: Exception) {
            logger.error(e) { "积存金每日任务未获取锁或执行失败" }
        }
    }

    private fun runDaily() {
        logger.info { "积存金每日定时任务开始" }
        val channels = channelMapper.selectList(
            KtQueryWrapper(AppGoldChannel()).eq(AppGoldChannel::enableFlag, 1),
        )
        channels.forEach { ch ->
            try {
                val code = ch.channelCode ?: return@forEach
                val dayBuckets = klineService.listLatest(code, "1day", 1)
                val prevClose = dayBuckets.firstOrNull()?.close
                if (prevClose != null) {
                    val quote = quoteMapper.selectOne(
                        KtQueryWrapper(AppGoldPriceQuote()).eq(AppGoldPriceQuote::channelId, ch.id),
                    )
                    if (quote != null) {
                        quote.prevClosePrice = prevClose
                        quote.intradayOpen = null
                        quote.intradayHigh = null
                        quote.intradayLow = null
                        quoteMapper.updateById(quote)
                    }
                }
            } catch (e: Exception) {
                logger.error(e) { "更新渠道 ${ch.channelCode} prevClosePrice 失败" }
            }
        }

        try {
            val positions = positionMapper.selectList(
                KtQueryWrapper(AppGoldPosition()).gt(AppGoldPosition::holdGrams, BigDecimal.ZERO),
            )
            val today = LocalDate.now()
            positions.forEach { pos ->
                pos.todayProfit = BigDecimal.ZERO
                pos.todayProfitDate = today
                positionMapper.updateById(pos)
            }
            logger.info { "重置 ${positions.size} 条持仓 today_profit" }
        } catch (e: Exception) {
            logger.error(e) { "重置 today_profit 失败" }
        }
        logger.info { "积存金每日定时任务完成" }
    }
}
