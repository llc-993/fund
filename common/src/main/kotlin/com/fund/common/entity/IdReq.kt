package com.fund.common.entity

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "ID请求参数")
open class IdReq {

    @Schema(description = "记录ID", required = true, example = "1")
    var id: Long? = null

}