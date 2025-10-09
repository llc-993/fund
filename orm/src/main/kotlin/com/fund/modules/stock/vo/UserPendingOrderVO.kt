package com.fund.modules.stock.vo

import com.fasterxml.jackson.annotation.JsonFormat
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

class UserPendingOrderVO : Serializable {

    var id: Int? = null
    var userId: Int? = null
    var stockId: String? = null
    var futuresCode: String? = null
    var indexCode: String? = null
    var stockName: String? = null

    var buyNum: Int? = null

    var buyType: Int? = null

    var lever: Int? = null

    var profitTarget: BigDecimal? = null

    var stopTarget: BigDecimal? = null

    var nowPrice: BigDecimal? = null

    var targetPrice: BigDecimal? = null

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    var addTime: Date? = null

    var status: Int? = null

    var stockType: String? = null

    var failReason: String? = null

    var orderNo: String? = null

}