package com.fund.modules.sys.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "应用文档配置信息，用于配置APP的文案内容")
class ApiAppDocVO {
    
    @Schema(
        description = "文档默认标题",
        nullable = true
    )
    var defaultTitle: String? = ""

    @Schema(
        description = "文档默认内容，支持HTML格式",
        nullable = true
    )
    var defaultContent: String? = ""

    @Schema(
        description = "资源服务器域名，用于访问静态资源",
        nullable = true
    )
    var sourceHost: String? = ""

    @Schema(
        description = "资源地址路径，相对于资源服务器的路径",
        nullable = true
    )
    var sourceUri: String? = ""
}
