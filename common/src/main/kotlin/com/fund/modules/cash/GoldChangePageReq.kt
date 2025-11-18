package com.fund.modules.cash

import com.fund.common.entity.PageReq
import org.springframework.format.annotation.DateTimeFormat
import java.util.Date

/**
 * 查询账变信息分页参数
 */
class GoldChangePageReq : PageReq() {
    /**
     * 会员id
     */
    var userId: Long? = null

    /**
     * 变动类型
     */
    var changeType: Int? = null

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var startTime: Date? = null

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var endTime: Date? = null
}