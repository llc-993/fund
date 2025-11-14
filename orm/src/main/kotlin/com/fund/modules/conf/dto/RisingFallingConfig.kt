package com.fund.modules.conf.dto

import com.fund.modules.conf.ant.DefaultValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "涨跌板块配置信息")
class RisingFallingConfig {

    /**
     * 涨跌板块最小申购数量
     */
    @Schema(description = "涨跌板块最小申购数量", example = "1")
    @DefaultValue("1")
    var risingFallingMinNum: String = "1"

    /**
     * 涨跌板块最大申购数量
     */
    @Schema(description = "涨跌板块最大申购数量", example = "10000")
    @DefaultValue("10000")
    var risingFallingMaxNum: String = "10000"

}
