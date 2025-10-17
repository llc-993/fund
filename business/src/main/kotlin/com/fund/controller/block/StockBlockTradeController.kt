package com.fund.controller.block

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.modules.block.BlockTradeApplyRequest
import com.fund.modules.block.BlockTradeUpdateRequest
import com.fund.modules.block.model.StockBlockTrade
import com.fund.modules.block.service.StockBlockTradeService
import com.fund.modules.block.service.StockBlockTradeSubscriptionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 大宗交易
 */
@RestController
@RequestMapping("/block")
class StockBlockTradeController(
    private val stockBlockTradeService: StockBlockTradeService,
    private val stockBlockTradeSubscriptionService: StockBlockTradeSubscriptionService
) {

    /**
     * 列表
     */
    @GetMapping("list")
    fun list(): R<Any> {
        val blockTrades = stockBlockTradeService.list(
            KtQueryWrapper(StockBlockTrade())
                .eq(StockBlockTrade::status, 1)
        )
        return R.success(blockTrades)
    }

    /**
     * 申购
     */
    @SaCheckLogin
    @PostMapping("apply")
    fun apply(@RequestBody req: BlockTradeApplyRequest): R<Any> {
        return stockBlockTradeSubscriptionService.apply(req, StpUtil.getLoginIdAsLong())
    }

    /**
     * 申购历史
     */
    @SaCheckLogin
    @GetMapping("history")
    fun history(): R<Any> {
        return stockBlockTradeSubscriptionService.history(StpUtil.getLoginIdAsLong())
    }

    /**
     * 修改申购
     */
    @SaCheckLogin
    @PostMapping("update")
    fun update(@RequestBody req: BlockTradeUpdateRequest): R<Any> {
        return stockBlockTradeSubscriptionService.update(req, StpUtil.getLoginIdAsLong())
    }

}