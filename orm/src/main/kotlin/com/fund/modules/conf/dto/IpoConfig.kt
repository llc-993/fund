package com.fund.modules.conf.dto

import com.fund.modules.conf.ant.DefaultValue

class IpoConfig {

    /**
     * IPO最小申购数量
     */
    @DefaultValue("1")
    var ipoMinNum: String = "1"

    /**
     * IPO最大申购数量
     */
    @DefaultValue("10000")
    var ipoMaxNum: String = "10000"

}

