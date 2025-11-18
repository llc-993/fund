package com.fund.modules.cash

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.util.Date

@Schema(description = "提现审核请求对象")
class CashOutReviewReq {
    /**
     * 提现申请id
     */
    var id: @NotNull Long? = null

    /**
     * 打款时间(不填则为当前时间)
     */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")
    var remitTime: Date? = null

    /**
     * 是否受理
     */
    var pass: @NotNull Boolean? = null

    /**
     * 失败原因,如果有
     */
    var reason: String? = null

    /**
     * 交易hash
     */
    var hash: String? = null
}