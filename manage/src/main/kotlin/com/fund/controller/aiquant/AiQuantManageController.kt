package com.fund.controller.aiquant

import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.modules.aiquant.model.AppAiQuantCycle
import com.fund.modules.aiquant.model.AppAiQuantGlobalConfig
import com.fund.modules.aiquant.model.AppAiQuantOrder
import com.fund.modules.aiquant.model.AppAiQuantServiceChannel
import com.fund.modules.aiquant.request.AiQuantAuditManageReq
import com.fund.modules.aiquant.request.AiQuantChannelPageReq
import com.fund.modules.aiquant.request.AiQuantChannelSaveReq
import com.fund.modules.aiquant.request.AiQuantCyclePageManageReq
import com.fund.modules.aiquant.request.AiQuantFinishManageReq
import com.fund.modules.aiquant.request.AiQuantGlobalConfigUpdateReq
import com.fund.modules.aiquant.request.AiQuantOrderCreateManageReq
import com.fund.modules.aiquant.request.AiQuantOrderPageManageReq
import com.fund.modules.aiquant.request.AiQuantOrderUpdateManageReq
import com.fund.modules.aiquant.service.AppAiQuantCycleService
import com.fund.modules.aiquant.service.AppAiQuantGlobalConfigService
import com.fund.modules.aiquant.service.AppAiQuantOrderService
import com.fund.modules.aiquant.service.AppAiQuantServiceChannelService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** AI 量化管理端：审核、建单、更新订单、完结与配置 */
@Tag(name = "AI量化管理", description = "股票AI量化后台操作")
@RestController
@RequestMapping("/aiQuant")
class AiQuantManageController(
    private val aiQuantCycleService: AppAiQuantCycleService,
    private val aiQuantOrderService: AppAiQuantOrderService,
    private val aiQuantGlobalConfigService: AppAiQuantGlobalConfigService,
    private val aiQuantChannelService: AppAiQuantServiceChannelService,
) {

    @Operation(summary = "周期分页")
    @GetMapping("/cycle/page")
    fun cyclePage(query: AiQuantCyclePageManageReq): R<Page<AppAiQuantCycle>> {
        return R.success(aiQuantCycleService.managePage(query))
    }

    @Operation(summary = "审核周期")
    @PostMapping("/cycle/audit")
    fun auditCycle(@RequestBody req: AiQuantAuditManageReq): R<AppAiQuantCycle> {
        val adminId = StpUtil.getLoginIdAsLong()
        return R.success(aiQuantCycleService.audit(adminId, req))
    }

    @Operation(summary = "完结周期（释放本金并结算盈亏，订单对用户可见）")
    @PostMapping("/cycle/finish")
    fun finishCycle(@RequestBody req: AiQuantFinishManageReq): R<AppAiQuantCycle> {
        val adminId = StpUtil.getLoginIdAsLong()
        return R.success(aiQuantCycleService.finish(adminId, req))
    }

    @Operation(summary = "创建展示订单（审核通过且尚无订单）")
    @PostMapping("/order/create")
    fun createOrder(@RequestBody req: AiQuantOrderCreateManageReq): R<AppAiQuantOrder> {
        val adminId = StpUtil.getLoginIdAsLong()
        return R.success(aiQuantOrderService.createByAdmin(adminId, req))
    }

    /** 多用于补充卖出时间与卖出价以便完结结算 */
    @Operation(summary = "更新展示订单")
    @PostMapping("/order/update")
    fun updateOrder(@RequestBody req: AiQuantOrderUpdateManageReq): R<AppAiQuantOrder> {
        val adminId = StpUtil.getLoginIdAsLong()
        return R.success(aiQuantOrderService.updateByAdmin(adminId, req))
    }

    @Operation(summary = "订单分页")
    @GetMapping("/order/page")
    fun orderPage(query: AiQuantOrderPageManageReq): R<Page<AppAiQuantOrder>> {
        return R.success(aiQuantOrderService.managePage(query))
    }

    @Operation(summary = "全局配置详情")
    @GetMapping("/globalConfig")
    fun globalConfig(): R<AppAiQuantGlobalConfig> {
        return R.success(aiQuantGlobalConfigService.loadOrCreate())
    }

    @Operation(summary = "保存全局配置")
    @PostMapping("/globalConfig")
    fun saveGlobalConfig(@RequestBody req: AiQuantGlobalConfigUpdateReq): R<AppAiQuantGlobalConfig> {
        return R.success(aiQuantGlobalConfigService.patch(req))
    }

    @Operation(summary = "渠道分页列表")
    @GetMapping("/channel/page")
    fun channelPage(query: AiQuantChannelPageReq): R<Page<AppAiQuantServiceChannel>> {
        return R.success(aiQuantChannelService.managePage(query))
    }

    @Operation(summary = "新增或修改渠道")
    @PostMapping("/channel/save")
    fun channelSave(@RequestBody req: AiQuantChannelSaveReq): R<AppAiQuantServiceChannel> {
        return R.success(aiQuantChannelService.upsert(req))
    }
}
