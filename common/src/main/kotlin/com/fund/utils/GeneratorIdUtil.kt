package com.fund.utils

import cn.hutool.core.util.IdUtil

object GeneratorIdUtil {

    fun generateId(): String {
        return IdUtil.getSnowflakeNextIdStr()
    }

    fun generateLongId(): Long {
        return IdUtil.getSnowflakeNextId()
    }
}