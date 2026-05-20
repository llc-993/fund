package com.fund.modules.aiquant

/**
 * AI 量化周期阶段：0待审 1处理中 2已完成 -1驳回
 */
object AiQuantCyclePhase {
    const val PENDING_AUDIT = 0
    const val PROCESSING = 1
    const val FINISHED = 2
    const val REJECTED = -1
}
