package com.fund.modules.kline.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * K线时间聚合工具类
 */
object KlineAggregator {
    
    /**
     * K线时间间隔枚举
     */
    enum class Interval(val code: String, val minutes: Long) {
        ONE_MIN("1min", 1),
        FIVE_MIN("5min", 5),
        THIRTY_MIN("30min", 30),
        ONE_HOUR("1h", 60),
        ONE_DAY("1day", 1440),
        ONE_WEEK("1week", 10080),
        ONE_MONTH("1month", 43200)
    }
    
    /**
     * 获取所有支持的时间间隔
     */
    fun getAllIntervals(): List<String> {
        return Interval.values().map { it.code }
    }
    
    /**
     * 根据股票市场标识获取时区
     */
    fun getTimeZoneByMarket(market: String): ZoneId {
        return when (market.uppercase()) {
            "US" -> ZoneId.of("America/New_York")
            "CN" -> ZoneId.of("Asia/Shanghai")
            "IN" -> ZoneId.of("Asia/Kolkata")
            "DE" -> ZoneId.of("Europe/Berlin")
            "HK" -> ZoneId.of("Asia/Hong_Kong")
            "JP" -> ZoneId.of("Asia/Tokyo")
            else -> ZoneId.of("UTC")
        }
    }
    
    /**
     * 对齐时间戳到指定周期的开始时间
     * @param timestamp 当前时间戳（毫秒）
     * @param interval K线周期
     * @param zoneId 时区
     * @return 对齐后的时间戳（秒）
     */
    fun alignTimestamp(timestamp: Long, interval: String, zoneId: ZoneId): Long {
        val zonedDateTime = Instant.ofEpochMilli(timestamp)
            .atZone(zoneId)
        
        return when (interval) {
            "1min" -> alignToMinute(zonedDateTime, 1)
            "5min" -> alignToMinute(zonedDateTime, 5)
            "30min" -> alignToMinute(zonedDateTime, 30)
            "1h" -> alignToHour(zonedDateTime)
            "1day" -> alignToDay(zonedDateTime)
            "1week" -> alignToWeek(zonedDateTime)
            "1month" -> alignToMonth(zonedDateTime)
            else -> zonedDateTime.toEpochSecond()
        }
    }
    
    /**
     * 对齐到分钟
     */
    private fun alignToMinute(time: ZonedDateTime, minutes: Int): Long {
        val minute = time.minute
        val alignedMinute = (minute / minutes) * minutes
        
        return time
            .withMinute(alignedMinute)
            .withSecond(0)
            .withNano(0)
            .toEpochSecond()
    }
    
    /**
     * 对齐到小时
     */
    private fun alignToHour(time: ZonedDateTime): Long {
        return time
            .truncatedTo(ChronoUnit.HOURS)
            .toEpochSecond()
    }
    
    /**
     * 对齐到天（当日0点）
     */
    private fun alignToDay(time: ZonedDateTime): Long {
        return time
            .truncatedTo(ChronoUnit.DAYS)
            .toEpochSecond()
    }
    
    /**
     * 对齐到周（周一0点）
     */
    private fun alignToWeek(time: ZonedDateTime): Long {
        return time
            .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
            .truncatedTo(ChronoUnit.DAYS)
            .toEpochSecond()
    }
    
    /**
     * 对齐到月（月初0点）
     */
    private fun alignToMonth(time: ZonedDateTime): Long {
        return time
            .withDayOfMonth(1)
            .truncatedTo(ChronoUnit.DAYS)
            .toEpochSecond()
    }
}

