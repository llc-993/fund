package com.fund.modules.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.io.Serializable

@Schema(description = "编辑会员信息请求")
class AdminEditAppUserReq: Serializable {

    /**
     * 会员Id
     */
    var userId: @NotNull Long? = null

    /**
     * 手机号
     */
    var mobilePhone: String? = null

    /**
     * 登录密码
     */
    var password: String? = null

    /**
     * 交易密码
     */
    var moneyPassword: String? = null

    /**
     * 会员等级
     */
    var levelWeights: Int? = null

    /**
     * 是否假人 正常 0 假人 1
     */
    var userGroup: Int? = null

    /**
     * 状态 是否冻结
     */
    var isFrozen: Boolean? = null

    /**
     * 是否允许交易
     */
    var tradable: Boolean? = null

    /**
     * 是否允许提现
     */
    var cashable: Boolean? = null
}