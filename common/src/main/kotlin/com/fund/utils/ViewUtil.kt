package com.fund.utils

import java.math.BigDecimal

object ViewUtil {

    // 判断是否大于0 不等于null的前提
    fun gtZero(money: BigDecimal?): Boolean {
        return money != null && money.compareTo(BigDecimal.ZERO) > 0
    }


    // 是否小于0 不等于 null的前提
    fun ltZero(money: BigDecimal?): Boolean {
        return money != null && money.compareTo(BigDecimal.ZERO) < 0
    }

    // 非0 不等于 null的前提
    fun notZero(money: BigDecimal?): Boolean {
        return money != null && money.compareTo(BigDecimal.ZERO) != 0
    }
}