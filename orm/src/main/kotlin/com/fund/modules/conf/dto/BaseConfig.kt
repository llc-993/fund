package com.fund.modules.conf.dto

import com.fund.modules.conf.ant.DefaultValue
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "系统基础配置信息")
class BaseConfig {

    // 注册奖励
    @Schema(description = "注册奖励金额", example = "0")
    var regReward: BigDecimal = BigDecimal.ZERO

    //注册频率限制/天
    @Schema(description = "每日注册频率限制", example = "999")
    var regLimitDay: Int = 999

    //默认头像
    @Schema(description = "默认用户头像路径", example = "/")
    var defaultAvatar: String = "/"

    /**
     * 最小购买数量
     */
    @Schema(description = "最小购买数量", example = "1")
    @DefaultValue("1")
    var buyMinNum: BigDecimal = BigDecimal.ZERO

    /**
     * 最大购买数量
     */
    @Schema(description = "最大购买数量", example = "999999")
    @DefaultValue("999999")
    var buyMaxNum: BigDecimal = BigDecimal.valueOf(999999)

    /**
     * 买入手续费率
     */
    @Schema(description = "买入手续费率", example = "0.001")
    @DefaultValue("0.001")
    var buyFeeRate: BigDecimal = BigDecimal("0.001")

    /**
     * 留仓费率
     */
    @Schema(description = "留仓费率", example = "0.0001")
    @DefaultValue("0.0001")
    var stayFeeRate: BigDecimal = BigDecimal("0.0001")

    /**
     * 印花税费率
     */
    @Schema(description = "印花税费率", example = "0.001")
    @DefaultValue("0.001")
    var dutyFeeRate: BigDecimal = BigDecimal("0.001")

    /**
     * 最小提现金额
     */
    @Schema(description = "最小提现金额", example = "10")
    @DefaultValue("10")
    var cashOutMinAmount: String = "10"

    /**
     * 最大提现金额
     */
    @Schema(description = "最大提现金额", example = "1000000")
    @DefaultValue("1000000")
    var cashOutMaxAmount: String = "1000000"

    /**
     * 提现手续费率
     */
    @Schema(description = "提现手续费率", example = "0.01")
    @DefaultValue("0.01")
    var cashOutFeeRate: String = "0.01"

}