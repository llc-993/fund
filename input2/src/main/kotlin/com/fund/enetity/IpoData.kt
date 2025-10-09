package com.fund.enetity

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

class IpoData : Serializable{

    @JSONField(name = "country")
    var country: String = "" // 国家

    @JSONField(name = "name")
    var name: String = "" // 名字

    @JSONField(name = "symbol")
    var symbol: String = "" // 产品代码

    @JSONField(name = "openDate")
    var openDate: String = "" // 开始时间GTM+0

    @JSONField(name = "closeDate")
    var closeDate: String = "" // 结束时间GTM+0

    @JSONField(name = "listingDate")
    var listingDate: String = "" // 上市时间GTM+0

    @JSONField(name = "issuePrice")
    var issuePrice: String? = null // 价格

    @JSONField(name = "issueSize")
    var issueSize: String? = null // 认购数量

    @JSONField(name = "lotSize")
    var lotSize: String? = null // 最小数量

    @JSONField(name = "exchange")
    var exchange: String? = null // 交易所

    @JSONField(name = "weight")
    var weight: Int = 0 // 无意义

    @JSONField(name = "createTime")
    var createTime: String = "" // 本地更新时间GMT+0

    @JSONField(name = "remarks")
    var remarks: String? = null // 无意义备用
}