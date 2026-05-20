package com.fund.modules.gold

/** 积存金订单状态 */
object GoldOrderStatus {
    /** 处理中 */
    const val PROCESSING: Int = 0

    /** 已成交 */
    const val FINISHED: Int = 1

    /** 失败 */
    const val FAILED: Int = -1
}
