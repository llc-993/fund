package com.fund.modules.sys.dto

data class AdminInfo(
    val roleCode: String = "",
    val agentType: Int = 0, // -1 不是代理 0 总代 1 一级代理  2 二级代理
    val loginName: String = "",
    val topId: Long = -1L
)
