package com.fund.modules.stock.vo

import java.math.BigDecimal

class UserOrderVo {


    var id: String? = null //关联的数据的id

    var userId: String? = null

    var stockCode: String? = null

    var stockType: String? = null // us hk my th ph id futures index

    var buyType: String? = null //1-买涨 2-买跌

    var dataType: String? = null //1-挂单购入 2-止盈平仓 3-止损平仓 4-留仓到期平仓 5-平仓线强制平仓


    //===== 以上必传项======
    var amount: BigDecimal? = null //挂单价 止盈价 止损价    --当dataType为 留仓到期平仓、平仓线强制平仓 时可为空
    var oldNowPrice: BigDecimal? = null // 挂单时的现价

    var newPrice: BigDecimal? = null //最新价
    var preClosePx: BigDecimal? = null //昨日收盘价
    var hcrate: Float? = null //当前涨跌幅
}