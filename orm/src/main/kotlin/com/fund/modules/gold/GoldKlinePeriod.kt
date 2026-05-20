package com.fund.modules.gold

/**
 * 积存金 K 线区间：将界面 Tab 映射为 MongoDB interval 与查询条数。
 * interval 与集合后缀一致：1min / 5min / 30min / 1h / 1day / 1week / 1month。
 */
enum class GoldKlinePeriod(
    val code: String,
    val interval: String,
    val limit: Int,
) {
    /** 实时图：1 分钟桶 */
    REALTIME("realtime", "1min", 480),

    /** 近一月：日桶 */
    M1("m1", "1day", 30),

    /** 近三月：日桶 */
    M3("m3", "1day", 90),

    /** 近半年：日桶 */
    M6("m6", "1day", 180),

    /** 近一年：日桶 */
    Y1("y1", "1day", 365),
    ;

    companion object {
        fun ofOrDefault(code: String?): GoldKlinePeriod =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: REALTIME
    }
}
