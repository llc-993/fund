package com.fund.modules.sys.vo

import java.math.BigDecimal


class HomeData {

    /**
     * 总用户数量
     */
    var totalUser: Int? = null

    /**
     * 今日用户
     */
    var todayTotalUser: Int? = null

    /**
     * 总后台充值
     */
    var totalCashIn: BigDecimal? = null

    /**
     * 今日后台充值
     */
    var todayCashIn: BigDecimal? = null

    /**
     * 总提现
     */
    var totalCashOut: BigDecimal? = null

    /**
     * 今日提现
     */
    var todayCashOut: BigDecimal? = null
}