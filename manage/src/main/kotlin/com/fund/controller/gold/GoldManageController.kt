package com.fund.controller.gold

import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.modules.gold.model.AppGoldChannel
import com.fund.modules.gold.model.AppGoldGlobalConfig
import com.fund.modules.gold.model.AppGoldOrder
import com.fund.modules.gold.model.AppGoldPosition
import com.fund.modules.gold.model.AppGoldPriceQuote
import com.fund.modules.gold.request.GoldChannelPageReq
import com.fund.modules.gold.request.GoldChannelSaveReq
import com.fund.modules.gold.request.GoldGlobalConfigUpdateReq
import com.fund.modules.gold.request.GoldOrderPageReq
import com.fund.modules.gold.request.GoldPositionPageReq
import com.fund.modules.gold.request.GoldQuoteUpsertReq
import com.fund.modules.gold.service.AppGoldChannelService
import com.fund.modules.gold.service.AppGoldGlobalConfigService
import com.fund.modules.gold.service.AppGoldOrderService
import com.fund.modules.gold.service.AppGoldPositionService
import com.fund.modules.gold.service.AppGoldPriceQuoteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "积存金管理", description = "渠道/行情/订单/持仓/配置")
@RestController
@RequestMapping("/gold")
class GoldManageController(
    private val channelService: AppGoldChannelService,
    private val quoteService: AppGoldPriceQuoteService,
    private val orderService: AppGoldOrderService,
    private val positionService: AppGoldPositionService,
    private val globalConfigService: AppGoldGlobalConfigService,
) {

    @Operation(summary = "渠道分页")
    @GetMapping("/channel/page")
    fun channelPage(query: GoldChannelPageReq): R<Page<AppGoldChannel>> =
        R.success(channelService.managePage(query))

    @Operation(summary = "新增/修改渠道")
    @PostMapping("/channel/save")
    fun channelSave(@RequestBody req: GoldChannelSaveReq): R<AppGoldChannel> =
        R.success(channelService.upsert(req))

    @Operation(summary = "渠道启用/禁用")
    @PostMapping("/channel/toggle")
    fun channelToggle(@RequestParam id: Long, @RequestParam enable: Int): R<Boolean> =
        R.success(channelService.toggleEnable(id, enable))

    @Operation(summary = "写入实时金价")
    @PostMapping("/quote/upsert")
    fun upsertQuote(@RequestBody req: GoldQuoteUpsertReq): R<AppGoldPriceQuote> =
        R.success(quoteService.upsertQuote(req, StpUtil.getLoginIdAsLong()))

    @Operation(summary = "订单分页")
    @GetMapping("/order/page")
    fun orderPage(query: GoldOrderPageReq): R<Page<AppGoldOrder>> =
        R.success(orderService.managePage(query))

    @Operation(summary = "持仓分页")
    @GetMapping("/position/page")
    fun positionPage(query: GoldPositionPageReq): R<Page<AppGoldPosition>> =
        R.success(positionService.managePage(query))

    @Operation(summary = "全局配置")
    @GetMapping("/globalConfig")
    fun globalConfig(): R<AppGoldGlobalConfig> =
        R.success(globalConfigService.loadOrCreate())

    @Operation(summary = "保存全局配置")
    @PostMapping("/globalConfig")
    fun saveGlobalConfig(@RequestBody req: GoldGlobalConfigUpdateReq): R<AppGoldGlobalConfig> =
        R.success(globalConfigService.patch(req))
}
