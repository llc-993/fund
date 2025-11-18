package com.fund.modules.cash

import com.fund.common.entity.PageReq
import java.io.Serializable

class CashInOrderQueryPageReq : PageReq(), Serializable {

    /**
     * 用户名(模糊匹配)
     */
    var userAccount: String? = null

    /**
     * 订单编号
     */
    var orderNo: String? = null

    /**
     * 状态 待处理 2已锁定 3  已取消 4 已拒绝 5 已成功
     */
    var cashStatus: Int? = null

    /**
     * 申请时间-开始
     */
    var startTime: String? = null

    /**
     * 申请时间-结束
     */
    var endTime: String? = null

}