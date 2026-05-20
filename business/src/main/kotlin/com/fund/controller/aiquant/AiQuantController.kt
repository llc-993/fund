package com.fund.controller.aiquant

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.fund.common.entity.R
import com.fund.modules.aiquant.AiQuantHistoryItemVo
import com.fund.modules.aiquant.AiQuantUserSummaryVo
import com.fund.modules.aiquant.model.AppAiQuantCycle
import com.fund.modules.aiquant.request.AiQuantReserveReq
import com.fund.modules.aiquant.service.AppAiQuantCycleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** 股票 AI 量化用户端：预约、在途、历史与汇总 */
@Tag(name = "AI量化", description = "股票代码标的 AI 量化单轨预约与查询")
@RestController
@RequestMapping("/aiQuant")
class AiQuantController(
    private val aiQuantCycleService: AppAiQuantCycleService,
) {

    /** 冻结本金发起预约周期（待后台审核），同一用户同一时间仅允许一个在途周期 */
    @Operation(summary = "预约AI量化")
    @SaCheckLogin
    @PostMapping("/reserve")
    fun reserve(@RequestBody req: AiQuantReserveReq): R<AppAiQuantCycle> {
        val userId = StpUtil.getLoginIdAsLong()
        val c = aiQuantCycleService.submitReserve(userId, req)
        return R.success(c)
    }

    /** 待审 + 处理中（审核通过尚未完结）周期列表；用户完成前看不到展示订单明细 */
    @Operation(summary = "当前在途周期")
    @SaCheckLogin
    @GetMapping("/currentHolding")
    fun currentHolding(): R<List<AppAiQuantCycle>> {
        val userId = StpUtil.getLoginIdAsLong()
        return R.success(aiQuantCycleService.listUserCurrent(userId))
    }

    /** 已完结周期与对用户可见订单 */
    @Operation(summary = "历史记录")
    @SaCheckLogin
    @GetMapping("/history")
    fun history(): R<List<AiQuantHistoryItemVo>> {
        val userId = StpUtil.getLoginIdAsLong()
        val pairs = aiQuantCycleService.listUserHistory(userId)
        return R.success(pairs.map { AiQuantHistoryItemVo(it.first, it.second) })
    }

    @Operation(summary = "汇总指标")
    @SaCheckLogin
    @GetMapping("/summary")
    fun summary(): R<AiQuantUserSummaryVo> {
        val userId = StpUtil.getLoginIdAsLong()
        return R.success(aiQuantCycleService.summaryForUser(userId))
    }
}
