package com.fund.modules.cash

import com.fund.common.entity.PageReq
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "提现查询参数")
class CashOutOrderQueryPageReq: PageReq()  {

    /**
     * 关键词
     */
    var keyword: String? = null

    /**
     * 订单编号
     */
    var orderNo: String? = null

    /**
     * 状态 待处理 2已锁定 3  已取消 4 已拒绝 5 已成功"
     */
    var cashStatus: Int? = null

    /**
     * 正常 0 假人 1
     */
    var userGroup: Int? = null

    /**
     * 申请时间-开始
     */
    var startTime: String? = null

    /**
     * 申请时间-结束
     */
    var endTime: String? = null

}