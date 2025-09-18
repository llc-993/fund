package com.fund.modules.conf.dto

import com.fund.modules.conf.ant.DefaultValue
import java.math.BigDecimal

class BaseConfig {

    // 注册奖励
    var regReward: BigDecimal = BigDecimal.ZERO

    //注册频率限制/天
    var regLimitDay: Int = 999

    //默认头像
    var defaultAvatar: String = "/"

    /**
     * 最小购买数量
     */
    @DefaultValue("1")
    var buyMinNum: BigDecimal = BigDecimal.ZERO

    /**
     * 最大购买数量
     */
    @DefaultValue("999999")
    var buyMaxNum: BigDecimal = BigDecimal.valueOf(999999)

    /**
     * 买入手续费率
     */
    @DefaultValue("0.001")
    var buyFeeRate: BigDecimal = BigDecimal("0.001")

    /**
     * 留仓费率
     */
    @DefaultValue("0.0001")
    var stayFeeRate: BigDecimal = BigDecimal("0.0001")

    /**
     * 印花税费率
     */
    @DefaultValue("0.001")
    var dutyFeeRate: BigDecimal = BigDecimal("0.001")

}