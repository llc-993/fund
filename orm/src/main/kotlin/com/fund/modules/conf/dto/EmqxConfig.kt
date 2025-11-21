package com.fund.modules.conf.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "emqx配置")
class EmqxConfig {

    @Schema(description = "是否启用emqx")
    var emqxEnable: Boolean? = null

    @Schema(description = "emqx Api域名")
    var emqxApiHost: String? = null

    @Schema(description = "emqx api端口")
    var emqxApiPort: Int? = null

    @Schema(description = "emqx apiKey")
    var emqxApiKey: String? = null

    @Schema(description = "emqx 密钥")
    var emqxApiSecret: String? = null
}