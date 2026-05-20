package com.fund.modules.aiquant

import com.fund.modules.aiquant.model.AppAiQuantCycle
import com.fund.modules.aiquant.model.AppAiQuantOrder

/** 用户历史列表项（已完成周期与可见订单），供业务 API 序列化返回 */
data class AiQuantHistoryItemVo(
    val cycle: AppAiQuantCycle,
    val order: AppAiQuantOrder?,
)
