package com.fund.modules.gold.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.gold.GoldPositionApplyResult
import com.fund.modules.gold.model.AppGoldPosition
import com.fund.modules.gold.model.AppGoldChannel
import com.fund.modules.gold.request.GoldPositionPageReq
import com.fund.modules.gold.vo.GoldHoldingSummaryVo
import com.fund.modules.gold.vo.GoldPositionDetailVo
import java.math.BigDecimal

/** 积存金持仓 */
interface AppGoldPositionService : IService<AppGoldPosition> {
    fun findUserChannelPosition(userId: Long, channelId: Long): AppGoldPosition?
    fun listUserPositions(userId: Long): List<AppGoldPosition>

    fun applyBuy(
        userId: Long,
        cashWalletId: Long,
        goldWalletId: Long,
        channel: AppGoldChannel,
        grams: BigDecimal,
        principal: BigDecimal,
        buyFee: BigDecimal,
    ): GoldPositionApplyResult

    fun applySell(
        position: AppGoldPosition,
        sellGrams: BigDecimal,
        sellPrice: BigDecimal,
        sellFee: BigDecimal,
    ): GoldPositionApplyResult

    fun refreshValuation(position: AppGoldPosition, latestPrice: BigDecimal, prevClose: BigDecimal?)

    fun managePage(req: GoldPositionPageReq): Page<AppGoldPosition>
    fun summaryForUser(userId: Long): GoldHoldingSummaryVo
    fun detailForUser(userId: Long, channelId: Long): GoldPositionDetailVo?
}
