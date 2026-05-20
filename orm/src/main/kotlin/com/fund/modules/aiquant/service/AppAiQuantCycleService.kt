package com.fund.modules.aiquant.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.aiquant.AiQuantUserSummaryVo
import com.fund.modules.aiquant.model.AppAiQuantCycle
import com.fund.modules.aiquant.model.AppAiQuantOrder
import com.fund.modules.aiquant.request.AiQuantAuditManageReq
import com.fund.modules.aiquant.request.AiQuantCyclePageManageReq
import com.fund.modules.aiquant.request.AiQuantFinishManageReq
import com.fund.modules.aiquant.request.AiQuantReserveReq

/**
 * AI 量化周期：预约冻结、审核、完成结算
 */
interface AppAiQuantCycleService : IService<AppAiQuantCycle> {

    /** 用户预约：校验最低额与在途单笔、冻结本金、落库 phase=待审 */
    fun submitReserve(userId: Long, req: AiQuantReserveReq): AppAiQuantCycle

    /** 管理端审核（通过核定本金或驳回解冻） */
    fun audit(adminId: Long, req: AiQuantAuditManageReq): AppAiQuantCycle

    /** 管理端完结周期：校验订单卖出信息，释放本金、结算盈亏手续费、对用户可见订单 */
    fun finish(adminId: Long, req: AiQuantFinishManageReq): AppAiQuantCycle

    /** 用户在途：待审或处理中 */
    fun listUserCurrent(userId: Long): List<AppAiQuantCycle>

    /** 用户历史：已完成周期及关联可见订单（若有） */
    fun listUserHistory(userId: Long): List<Pair<AppAiQuantCycle, AppAiQuantOrder?>>

    fun summaryForUser(userId: Long): AiQuantUserSummaryVo

    fun managePage(query: AiQuantCyclePageManageReq): Page<AppAiQuantCycle>
}
