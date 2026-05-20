package com.fund.controller.gold

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.gold.GoldKlinePeriod
import com.fund.modules.gold.model.AppGoldGlobalConfig
import com.fund.modules.gold.model.AppGoldOrder
import com.fund.modules.gold.model.AppGoldPriceQuote
import com.fund.modules.gold.request.GoldBuyReq
import com.fund.modules.gold.request.GoldOrderPageReq
import com.fund.modules.gold.request.GoldSellReq
import com.fund.modules.gold.service.AppGoldChannelService
import com.fund.modules.gold.service.AppGoldGlobalConfigService
import com.fund.modules.gold.service.AppGoldOrderService
import com.fund.modules.gold.service.AppGoldPositionService
import com.fund.modules.gold.service.AppGoldPriceQuoteService
import com.fund.modules.gold.vo.GoldChannelHomeVo
import com.fund.modules.gold.vo.GoldHoldingSummaryVo
import com.fund.modules.gold.vo.GoldKlinePointVo
import com.fund.modules.gold.vo.GoldPositionDetailVo
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** 积存金用户端：行情、买卖、持仓、订单 */
@Tag(name = "积存金", description = "多渠道黄金积存（买入/卖出/持仓/行情）")
@RestController
@RequestMapping("/gold")
class GoldController(
    private val channelService: AppGoldChannelService,
    private val quoteService: AppGoldPriceQuoteService,
    private val orderService: AppGoldOrderService,
    private val positionService: AppGoldPositionService,
    private val globalConfigService: AppGoldGlobalConfigService,
) {

    @Operation(summary = "渠道列表")
    @GetMapping("/channels")
    fun channels(): R<List<GoldChannelHomeVo>> {
        val cfg = globalConfigService.loadOrCreate()
        if ((cfg.entryEnable ?: 1) != 1) {
            return R.success(emptyList())
        }
        return R.success(channelService.listEnabledForUser())
    }

    @Operation(summary = "渠道实时金价")
    @GetMapping("/quote")
    fun quote(@RequestParam channelId: Long): R<AppGoldPriceQuote?> =
        R.success(quoteService.getRealtime(channelId))

    @Operation(summary = "渠道历史K线")
    @GetMapping("/quote/kline")
    fun kline(
        @RequestParam channelId: Long,
        @RequestParam(required = false) period: String?,
    ): R<List<GoldKlinePointVo>> {
        val ch = channelService.getById(channelId) ?: throw BusinessException("渠道不存在")
        val list = quoteService.listKline(ch.channelCode!!, GoldKlinePeriod.ofOrDefault(period))
            .map {
                GoldKlinePointVo(
                    timestamp = it.timestamp,
                    open = it.open,
                    high = it.high,
                    low = it.low,
                    close = it.close,
                    volume = it.volume,
                )
            }
        return R.success(list)
    }

    @Operation(summary = "我的持仓汇总")
    @SaCheckLogin
    @GetMapping("/position/summary")
    fun summary(): R<GoldHoldingSummaryVo> =
        R.success(positionService.summaryForUser(StpUtil.getLoginIdAsLong()))

    @Operation(summary = "我的渠道持仓详情")
    @SaCheckLogin
    @GetMapping("/position/detail")
    fun detail(@RequestParam channelId: Long): R<GoldPositionDetailVo?> =
        R.success(positionService.detailForUser(StpUtil.getLoginIdAsLong(), channelId))

    @Operation(summary = "积存金买入")
    @SaCheckLogin
    @PostMapping("/buy")
    fun buy(@RequestBody req: GoldBuyReq): R<AppGoldOrder> =
        R.success(orderService.userBuy(StpUtil.getLoginIdAsLong(), req))

    @Operation(summary = "积存金卖出")
    @SaCheckLogin
    @PostMapping("/sell")
    fun sell(@RequestBody req: GoldSellReq): R<AppGoldOrder> =
        R.success(orderService.userSell(StpUtil.getLoginIdAsLong(), req))

    @Operation(summary = "我的交易记录")
    @SaCheckLogin
    @GetMapping("/order/page")
    fun orderPage(query: GoldOrderPageReq): R<Page<AppGoldOrder>> =
        R.success(orderService.pageMyOrders(StpUtil.getLoginIdAsLong(), query))

    @Operation(summary = "积存金全局配置")
    @GetMapping("/globalConfig")
    fun globalConfig(): R<AppGoldGlobalConfig> =
        R.success(globalConfigService.loadOrCreate())
}
