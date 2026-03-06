package com.fund.common.enums

/**
 * 行情 K 线周期映射
 *
 * @property interval 平台内部使用的周期标识
 * @property investingInterval Investing.com 使用的周期标识
 * @property psbanguInterval 其他第三方（psbangu）使用的周期标识
 */
enum class KlineEnum(
    val interval: String,
    val investingInterval: String,
    val psbanguInterval: String,
    val intervalTime: Long, // 周期时长（秒）
) {
    MIN_1("1min", "1", "1min", 60L),
    MIN_5("5min", "5", "5min", 300L),
    MIN_15("15min", "15", "15min", 900L),
    MIN_30("30min", "30", "30min", 1800L),
    H_1("1h", "60", "1h", 3600L),
    H_4("4h", "240", "4h", 14400L),
    D_1("1day", "D", "1day", 86400L),
    W_1("1week", "W", "1week", 604800L),
    M_1("1month", "M", "1month", 2592000L);

    companion object {
        private val BY_INTERVAL = entries.associateBy { it.interval.lowercase() }
        private val BY_INVESTING = entries.associateBy { it.investingInterval.lowercase() }
        private val BY_PSBANGU = entries.associateBy { it.psbanguInterval.lowercase() }

        fun fromInterval(interval: String?): KlineEnum? =
            interval?.lowercase()?.let(BY_INTERVAL::get)

        fun fromInvestingInterval(interval: String?): KlineEnum? =
            interval?.lowercase()?.let(BY_INVESTING::get)

        fun fromPsbanguInterval(interval: String?): KlineEnum? =
            interval?.lowercase()?.let(BY_PSBANGU::get)
    }
}