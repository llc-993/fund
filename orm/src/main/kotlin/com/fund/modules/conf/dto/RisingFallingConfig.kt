package com.fund.modules.conf.dto

import com.fund.modules.conf.ant.DefaultValue

class RisingFallingConfig {

    /**
     * 涨跌板块最小申购数量
     */
    @DefaultValue("1")
    var risingFallingMinNum: String = "1"

    /**
     * 涨跌板块最大申购数量
     */
    @DefaultValue("10000")
    var risingFallingMaxNum: String = "10000"

}
