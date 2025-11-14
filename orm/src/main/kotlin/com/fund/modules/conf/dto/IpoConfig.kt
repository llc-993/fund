package com.fund.modules.conf.dto

import com.fund.modules.conf.ant.DefaultValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "新股申购配置信息")
class IpoConfig {

    /**
     * IPO最小申购数量
     */
    @Schema(description = "IPO最小申购数量", example = "1")
    @DefaultValue("1")
    var ipoMinNum: String = "1"

    /**
     * IPO最大申购数量
     */
    @Schema(description = "IPO最大申购数量", example = "10000")
    @DefaultValue("10000")
    var ipoMaxNum: String = "10000"

}

