package com.fund.modules.conf.enum

import com.fund.modules.conf.ant.DefaultValue
import com.fund.modules.conf.dto.BaseConfig
import com.fund.modules.conf.dto.StockMarketConfig
import java.util.Arrays
import java.util.stream.Collectors
import kotlin.reflect.KProperty
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.jvmErasure

enum class AppConfigCode(prop: KProperty<*>) {

    REG_LIMIT_DAY(BaseConfig::regLimitDay),
    DEFAULT_AVATAR(BaseConfig::defaultAvatar),

    REG_REWARD(BaseConfig::regReward),

    // 最小购买数量
    BUY_MIN_NUM(BaseConfig::buyMinNum),

    // 最大购买数量
    BUY_MAX_NUM(BaseConfig::buyMaxNum),

    // 美国股票市场配置
    US_TIMEZONE(StockMarketConfig::usTimezone),
    US_MORNING_OPEN(StockMarketConfig::usMorningOpen),
    US_AFTERNOON_OPEN(StockMarketConfig::usAfternoonOpen),
    US_MORNING_CLOSE(StockMarketConfig::usMorningClose),
    US_AFTERNOON_CLOSE(StockMarketConfig::usAfternoonClose),

    // 中国股票市场配置
    CN_TIMEZONE(StockMarketConfig::cnTimezone),
    CN_MORNING_OPEN(StockMarketConfig::cnMorningOpen),
    CN_AFTERNOON_OPEN(StockMarketConfig::cnAfternoonOpen),
    CN_MORNING_CLOSE(StockMarketConfig::cnMorningClose),
    CN_AFTERNOON_CLOSE(StockMarketConfig::cnAfternoonClose),

    // 印度股票市场配置
    IN_TIMEZONE(StockMarketConfig::inTimezone),
    IN_MORNING_OPEN(StockMarketConfig::inMorningOpen),
    IN_AFTERNOON_OPEN(StockMarketConfig::inAfternoonOpen),
    IN_MORNING_CLOSE(StockMarketConfig::inMorningClose),
    IN_AFTERNOON_CLOSE(StockMarketConfig::inAfternoonClose),

    // 德国股票市场配置
    DE_TIMEZONE(StockMarketConfig::deTimezone),
    DE_MORNING_OPEN(StockMarketConfig::deMorningOpen),
    DE_AFTERNOON_OPEN(StockMarketConfig::deAfternoonOpen),
    DE_MORNING_CLOSE(StockMarketConfig::deMorningClose),
    DE_AFTERNOON_CLOSE(StockMarketConfig::deAfternoonClose),

    // 各市场Lot单位配置
    US_LOT_UNIT(StockMarketConfig::usLotUnit),
    CN_LOT_UNIT(StockMarketConfig::cnLotUnit),
    IN_LOT_UNIT(StockMarketConfig::inLotUnit),
    DE_LOT_UNIT(StockMarketConfig::deLotUnit),

    // 各市场最小购买金额配置
    US_MIN_BUY_AMOUNT(StockMarketConfig::usMinBuyAmount),
    CN_MIN_BUY_AMOUNT(StockMarketConfig::cnMinBuyAmount),
    IN_MIN_BUY_AMOUNT(StockMarketConfig::inMinBuyAmount),
    DE_MIN_BUY_AMOUNT(StockMarketConfig::deMinBuyAmount),

    // 买入手续费率
    BUY_FEE_RATE(BaseConfig::buyFeeRate),

    // 留仓费率
    STAY_FEE_RATE(BaseConfig::stayFeeRate),

    // 印花税费率
    DUTY_FEE_RATE(BaseConfig::dutyFeeRate),
    ;

    var code: String
    var group: Class<*>
    var defaultValue: String = ""


    companion object {
        /**
         * 按照分组获取配置
         * @param group
         * @return
         */
        fun getGroup(group: Class<*>?): List<AppConfigCode> {
            return Arrays.stream(AppConfigCode.entries.toTypedArray()).filter { c: AppConfigCode -> group == c.group }
                .collect(Collectors.toList())
        }
    }

    init {
        // 从属性中获取字段名称作为 code
        this.code = prop.name
        // 从属性中读取 DefaultValue注解
        val ann = prop.findAnnotation<DefaultValue>()
        // 如果注解存在，将注解中的值赋值给 当前枚举的defaultValue
        ann?.let {
            this.defaultValue = it.value
        }
        // 获取父级class, class作为分组。
        val receiver = (prop.instanceParameter ?: prop.extensionReceiverParameter)!!
        val receiverClass = receiver.type.jvmErasure.java
        this.group = receiverClass
    }
}