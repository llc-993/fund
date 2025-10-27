package com.fund.common.entity

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 分页参数
 */
@Schema(description = "分页请求参数")
open class PageReq {

    @Schema(description = "页码，从1开始", example = "1", defaultValue = "1")
    var pageNum: Long = 1

    @Schema(description = "每页记录数", example = "10", defaultValue = "10")
    var pageSize: Long = 10
}
