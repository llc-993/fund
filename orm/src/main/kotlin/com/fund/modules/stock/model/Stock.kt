package com.fund.modules.stock.model;

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.math.BigDecimal

/**
 * <p>
 * 股票行情数据表
 * </p>
 *
 * @author 书记
 * @since 2025-08-12
 */
@TableName("stock")
class Stock : Serializable {

    /**
     * 股票唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 股票名称
     */
    @TableField("name")
    var name: String? = null

    /**
     * 股票代码
     */
    @TableField("symbol")
    var symbol: String? = null

    /**
     * 是否为CFD合约
     */
    @TableField("is_cfd")
    var isCfd: String? = null

    /**
     * 当日最高价
     */
    @TableField("high")
    var high: BigDecimal? = null

    /**
     * 当日最低价
     */
    @TableField("low")
    var low: BigDecimal? = null

    /**
     * 最新成交价
     */
    @TableField("last")
    var last: BigDecimal? = null

    /**
     * 价格小数位数
     */
    @TableField("last_pair_decimal")
    var lastPairDecimal: Int? = null

    /**
     * 当日价格变动额
     */
    @TableField("chg")
    var chg: BigDecimal? = null

    /**
     * 当日价格变动百分比(%)
     */
    @TableField("chg_pct")
    var chgPct: BigDecimal? = null

    /**
     * 当日成交量
     */
    @TableField("volume")
    var volume: Long? = null

    /**
     * 平均成交量
     */
    @TableField("avg_volume")
    var avgVolume: Long? = null

    /**
     * 时间戳(Unix)
     */
    @TableField("time")
    var time: Long? = null

    /**
     * 是否开市(0=休市, 1=开市)
     */
    @TableField("is_open")
    var isOpen: String? = null

    /**
     * 股票详情URL(相对路径)
     */
    @TableField("url")
    var url: String? = null

    /**
     * 国家代码(ISO两位)
     */
    @TableField("flag")
    var flag: String? = null

    /**
     * 国家名称(翻译)
     */
    @TableField("country_name_translated")
    var countryNameTranslated: String? = null

    /**
     * 交易所ID
     */
    @TableField("exchange_id")
    var exchangeId: String? = null

    /**
     * 当日涨跌幅(%)
     */
    @TableField("performance_day")
    var performanceDay: BigDecimal? = null

    /**
     * 近1周涨跌幅(%)
     */
    @TableField("performance_week")
    var performanceWeek: BigDecimal? = null

    /**
     * 近1月涨跌幅(%)
     */
    @TableField("performance_month")
    var performanceMonth: BigDecimal? = null

    /**
     * 年初至今涨跌幅(%)
     */
    @TableField("performance_ytd")
    var performanceYtd: BigDecimal? = null

    /**
     * 近1年涨跌幅(%)
     */
    @TableField("performance_year")
    var performanceYear: BigDecimal? = null

    /**
     * 近3年涨跌幅(%)
     */
    @TableField("performance_3year")
    var performance3year: BigDecimal? = null

    /**
     * 小时级技术面建议
     */
    @TableField("technical_hour")
    var technicalHour: String? = null

    /**
     * 日级技术面建议
     */
    @TableField("technical_day")
    var technicalDay: String? = null

    /**
     * 周级技术面建议
     */
    @TableField("technical_week")
    var technicalWeek: String? = null

    /**
     * 月级技术面建议
     */
    @TableField("technical_month")
    var technicalMonth: String? = null

    /**
     * 市值(货币单位同交易所)
     */
    @TableField("fundamental_market_cap")
    var fundamentalMarketCap: Long? = null

    /**
     * 营收(带单位的字符串)
     */
    @TableField("fundamental_revenue")
    var fundamentalRevenue: String? = null

    /**
     * 市盈率(PE)
     */
    @TableField("fundamental_ratio")
    var fundamentalRatio: BigDecimal? = null

    /**
     * Beta系数
     */
    @TableField("fundamental_beta")
    var fundamentalBeta: BigDecimal? = null

    /**
     * 产品类型(如Equities)
     */
    @TableField("pair_type")
    var pairType: String? = null

    @TableField("p_id")
    var pId:Long? = null

    @TableField("source_type")
    var sourceType:String? = null

    @TableField(exist = false)
    var askDepth: MutableMap<String, Any>? = null
    @TableField(exist = false)
    var bidDepth: MutableMap<String, Any>? = null

    override fun toString(): String {
        return "Stock(id=$id, name=$name, symbol=$symbol, isCfd=$isCfd, high=$high, low=$low, last=$last, lastPairDecimal=$lastPairDecimal, chg=$chg, chgPct=$chgPct, volume=$volume, avgVolume=$avgVolume, time=$time, isOpen=$isOpen, url=$url, flag=$flag, countryNameTranslated=$countryNameTranslated, exchangeId=$exchangeId, performanceDay=$performanceDay, performanceWeek=$performanceWeek, performanceMonth=$performanceMonth, performanceYtd=$performanceYtd, performanceYear=$performanceYear, performance3year=$performance3year, technicalHour=$technicalHour, technicalDay=$technicalDay, technicalWeek=$technicalWeek, technicalMonth=$technicalMonth, fundamentalMarketCap=$fundamentalMarketCap, fundamentalRevenue=$fundamentalRevenue, fundamentalRatio=$fundamentalRatio, fundamentalBeta=$fundamentalBeta, pairType=$pairType, pId=$pId, sourceType=$sourceType, askDepth=$askDepth, bidDepth=$bidDepth)"
    }


}
