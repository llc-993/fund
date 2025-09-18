package com.fund.modules.conf.dto

import com.fund.modules.conf.ant.DefaultValue

/**
 * 股票市场配置
 */
class StockMarketConfig {

    // 美国股票市场时区
    @DefaultValue("America/New_York")
    var usTimezone: String = "America/New_York"

    // 美国股票市场上午开盘时间
    @DefaultValue("09:30")
    var usMorningOpen: String = "09:30"

    // 美国股票市场下午开盘时间
    @DefaultValue("13:00")
    var usAfternoonOpen: String = "13:00"

    // 美国股票市场上午收盘时间
    @DefaultValue("12:00")
    var usMorningClose: String = "12:00"

    // 美国股票市场下午收盘时间
    @DefaultValue("16:00")
    var usAfternoonClose: String = "16:00"

    // 中国股票市场时区
    @DefaultValue("Asia/Shanghai")
    var cnTimezone: String = "Asia/Shanghai"

    // 中国股票市场上午开盘时间
    @DefaultValue("09:30")
    var cnMorningOpen: String = "09:30"

    // 中国股票市场下午开盘时间
    @DefaultValue("13:00")
    var cnAfternoonOpen: String = "13:00"

    // 中国股票市场上午收盘时间
    @DefaultValue("11:30")
    var cnMorningClose: String = "11:30"

    // 中国股票市场下午收盘时间
    @DefaultValue("15:00")
    var cnAfternoonClose: String = "15:00"

    // 印度股票市场时区
    @DefaultValue("Asia/Kolkata")
    var inTimezone: String = "Asia/Kolkata"

    // 印度股票市场上午开盘时间
    @DefaultValue("09:15")
    var inMorningOpen: String = "09:15"

    // 印度股票市场下午开盘时间
    @DefaultValue("14:00")
    var inAfternoonOpen: String = "14:00"

    // 印度股票市场上午收盘时间
    @DefaultValue("11:30")
    var inMorningClose: String = "11:30"

    // 印度股票市场下午收盘时间
    @DefaultValue("15:30")
    var inAfternoonClose: String = "15:30"

    // 德国股票市场时区
    @DefaultValue("Europe/Berlin")
    var deTimezone: String = "Europe/Berlin"

    // 德国股票市场上午开盘时间
    @DefaultValue("09:00")
    var deMorningOpen: String = "09:00"

    // 德国股票市场下午开盘时间
    @DefaultValue("13:00")
    var deAfternoonOpen: String = "13:00"

    // 德国股票市场上午收盘时间
    @DefaultValue("12:00")
    var deMorningClose: String = "12:00"

    // 德国股票市场下午收盘时间
    @DefaultValue("17:30")
    var deAfternoonClose: String = "17:30"

    // 各市场Lot单位配置
    @DefaultValue("1")
    var usLotUnit: String = "1"

    @DefaultValue("1")
    var cnLotUnit: String = "1"

    @DefaultValue("1")
    var inLotUnit: String = "1"

    @DefaultValue("1")
    var deLotUnit: String = "1"

    // 各市场最小购买金额配置
    @DefaultValue("10")
    var usMinBuyAmount: String = "10"

    @DefaultValue("100")
    var cnMinBuyAmount: String = "100"

    @DefaultValue("10")
    var inMinBuyAmount: String = "10"

    @DefaultValue("10")
    var deMinBuyAmount: String = "10"
}
