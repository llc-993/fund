package com.fund.modules.stock.model

import com.alibaba.fastjson.annotation.JSONField
import java.math.BigDecimal

/**
 * 通用股票数据模型
 * 用于Redis存储，不依赖特定模块的实体类
 */
class StockDataModel {
    /* 符号 */
    @JSONField(name = "code")
    var code: String? = null
    
    /* 时间戳,单位秒 */
    @JSONField(name = "tick")
    var tick: Long? = null
    
    /* 最新价 */
    @JSONField(name = "price")
    var price: BigDecimal? = null
    
    /* 相对成交量,较上次行情的成交量 */
    @JSONField(name = "NV")
    var nv: String? = null
    
    /* 成交量加权平均价 */
    @JSONField(name = "VWAP")
    var vwap: String? = null
    
    /* 今日成交量 */
    @JSONField(name = "volume")
    var volume: Long? = null
    
    /* 今日开盘价 */
    @JSONField(name = "open")
    var open: BigDecimal? = null
    
    /* 今日最高价 */
    @JSONField(name = "high")
    var high: BigDecimal? = null
    
    /* 今日最低价 */
    @JSONField(name = "low")
    var low: BigDecimal? = null
    
    /* 昨日收盘价 */
    @JSONField(name = "YS")
    var ys: BigDecimal? = null
    
    /* 持仓 */
    @JSONField(name = "position")
    var position: String? = null
    
    /* 今日成交额 */
    @JSONField(name = "amount")
    var amount: BigDecimal? = null
    
    /* 买盘单价,B1-B5，5档买盘单价 */
    @JSONField(name = "B1")
    var b1: String? = null
    
    /* 买盘量,B1V-B5V，5档买盘量 */
    @JSONField(name = "B1V")
    var b1v: String? = null
    
    /* 卖盘单价,S1-S5，5档卖盘单价 */
    @JSONField(name = "S1")
    var s1: String? = null
    
    /* 卖盘量,S1V-S5V，5档卖盘量 */
    @JSONField(name = "S1V")
    var s1v: String? = null
    
    /* 符号名称,符号对应的全称 */
    @JSONField(name = "name")
    var name: String? = null
    
    /* 品种标识符 */
    @JSONField(name = "Varieties")
    var varieties: String? = null
    
    /* 市场 */
    @JSONField(name = "Market")
    var market: String? = null
    
    /* T字段 */
    @JSONField(name = "T")
    var t: String? = null
    
    /* 合约类型 */
    @JSONField(name = "contract")
    var contract: String? = null
    
    /* 产品 */
    @JSONField(name = "Products")
    var products: String? = null
    
    /* 昨日收盘价 */
    @JSONField(name = "close")
    var close: BigDecimal? = null
    
    /* 较昨日涨跌值 */
    @JSONField(name = "change")
    var change: BigDecimal? = null
    
    /* 涨跌幅 */
    @JSONField(name = "up")
    var up: BigDecimal? = null
    
    /* 振幅 */
    @JSONField(name = "amplitude")
    var amplitude: BigDecimal? = null
    
    /* MRTA字段 */
    @JSONField(name = "MRTA")
    var mrta: Int? = null
    
    /* ZDT字段 */
    @JSONField(name = "ZDT")
    var zdt: Int? = null
    
    /* 交易信息,格式：时间戳,成交价,成交量,方向（1为主动卖，2为主动买） */
    @JSONField(name = "dealTransaction")
    var dealTransaction: String? = null

    override fun toString(): String {
        return "StockDataModel(code=$code, tick=$tick, price=$price, nv=$nv, vwap=$vwap, volume=$volume, open=$open, high=$high, low=$low, ys=$ys, position=$position, amount=$amount, b1=$b1, b1v=$b1v, s1=$s1, s1v=$s1v, name=$name, varieties=$varieties, market=$market, t=$t, contract=$contract, products=$products, close=$close, change=$change, up=$up, amplitude=$amplitude, mrta=$mrta, zdt=$zdt, dealTransaction=$dealTransaction)"
    }
}
