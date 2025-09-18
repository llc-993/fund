package com.fund.enetity

import com.alibaba.fastjson2.annotation.JSONField

/**
 * K线数据API响应模型
 */
data class KlineResponse(
    @JSONField(name = "status")
    val status: Int,

    @JSONField(name = "interval")
    val interval: String?,

    @JSONField(name = "market")
    val market: String?,

    @JSONField(name = "code")
    val code: String?,

    @JSONField(name = "message")
    val message: String?,

    @JSONField(name = "data")
    val data: String?
)