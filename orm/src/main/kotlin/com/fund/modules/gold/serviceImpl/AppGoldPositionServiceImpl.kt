package com.fund.modules.gold.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.modules.gold.GoldPositionApplyResult
import com.fund.modules.gold.mapper.AppGoldPositionMapper
import com.fund.modules.gold.model.AppGoldChannel
import com.fund.modules.gold.model.AppGoldPosition
import com.fund.modules.gold.model.AppGoldPriceQuote
import com.fund.modules.gold.request.GoldPositionPageReq
import com.fund.modules.gold.service.AppGoldChannelService
import com.fund.modules.gold.service.AppGoldPositionService
import com.fund.modules.gold.service.AppGoldPriceQuoteService
import com.fund.modules.gold.service.AppUserGoldWalletService
import com.fund.modules.gold.vo.GoldHoldingSummaryVo
import com.fund.modules.gold.vo.GoldPositionDetailVo
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Service
open class AppGoldPositionServiceImpl(
    private val channelService: AppGoldChannelService,
    private val quoteService: AppGoldPriceQuoteService,
    @Lazy private val goldWalletService: AppUserGoldWalletService,
) : ServiceImpl<AppGoldPositionMapper, AppGoldPosition>(),
    AppGoldPositionService {

    companion object {
        private const val EVALUATE_MIN_INTERVAL_SEC = 10L
    }

    override fun findUserChannelPosition(userId: Long, channelId: Long): AppGoldPosition? = getOne(
        KtQueryWrapper(AppGoldPosition())
            .eq(AppGoldPosition::userId, userId)
            .eq(AppGoldPosition::channelId, channelId)
            .last("limit 1"),
    )

    override fun listUserPositions(userId: Long): List<AppGoldPosition> = list(
        KtQueryWrapper(AppGoldPosition())
            .eq(AppGoldPosition::userId, userId)
            .eq(AppGoldPosition::status, 1),
    )

    override fun applyBuy(
        userId: Long,
        cashWalletId: Long,
        goldWalletId: Long,
        channel: AppGoldChannel,
        grams: BigDecimal,
        principal: BigDecimal,
        buyFee: BigDecimal,
    ): GoldPositionApplyResult {
        val pos = findUserChannelPosition(userId, channel.id!!) ?: AppGoldPosition().apply {
            this.userId = userId
            this.cashWalletId = cashWalletId
            this.goldWalletId = goldWalletId
            this.channelId = channel.id
            this.channelCode = channel.channelCode
            this.currencyCode = channel.currencyCode ?: "HKD"
            this.channelNameSnapshot = channel.channelName
            this.accountLabelSnapshot = channel.accountLabel
            this.holdGrams = BigDecimal.ZERO
            this.holdCost = BigDecimal.ZERO
            this.costAvgPrice = BigDecimal.ZERO
            this.cumulativeProfit = BigDecimal.ZERO
            this.cumulativeInvest = BigDecimal.ZERO
            this.cumulativeBuyFee = BigDecimal.ZERO
            this.cumulativeSellFee = BigDecimal.ZERO
            this.todayProfit = BigDecimal.ZERO
            this.lastMarketValue = BigDecimal.ZERO
            this.lastHoldingProfit = BigDecimal.ZERO
            this.status = 1
        }
        val before = pos.costAvgPrice ?: BigDecimal.ZERO
        pos.holdGrams = (pos.holdGrams ?: BigDecimal.ZERO).add(grams).setScale(16, RoundingMode.HALF_UP)
        pos.holdCost = (pos.holdCost ?: BigDecimal.ZERO).add(principal).setScale(16, RoundingMode.HALF_UP)
        pos.cumulativeInvest = (pos.cumulativeInvest ?: BigDecimal.ZERO).add(principal).setScale(16, RoundingMode.HALF_UP)
        pos.cumulativeBuyFee = (pos.cumulativeBuyFee ?: BigDecimal.ZERO).add(buyFee).setScale(16, RoundingMode.HALF_UP)
        pos.costAvgPrice = if (pos.holdGrams!!.signum() > 0) {
            pos.holdCost!!.divide(pos.holdGrams, 8, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }
        pos.lastBuyTime = LocalDateTime.now()
        if (pos.id == null) save(pos) else updateById(pos)
        return GoldPositionApplyResult(
            costAvgBefore = before,
            costAvgAfter = pos.costAvgPrice!!,
            sellCost = BigDecimal.ZERO,
            realizedProfit = BigDecimal.ZERO,
            position = pos,
        )
    }

    override fun applySell(
        position: AppGoldPosition,
        sellGrams: BigDecimal,
        sellPrice: BigDecimal,
        sellFee: BigDecimal,
    ): GoldPositionApplyResult {
        val before = position.costAvgPrice ?: BigDecimal.ZERO
        val sellCost = before.multiply(sellGrams).setScale(16, RoundingMode.HALF_UP)
        val sellAmount = sellPrice.multiply(sellGrams).setScale(16, RoundingMode.HALF_UP)
        val realized = sellAmount.subtract(sellCost).setScale(16, RoundingMode.HALF_UP)

        position.holdGrams = (position.holdGrams ?: BigDecimal.ZERO).subtract(sellGrams).setScale(16, RoundingMode.HALF_UP)
        position.holdCost = (position.holdCost ?: BigDecimal.ZERO).subtract(sellCost).setScale(16, RoundingMode.HALF_UP)
        if (position.holdGrams!!.signum() <= 0) {
            position.holdGrams = BigDecimal.ZERO
            position.holdCost = BigDecimal.ZERO
            position.costAvgPrice = BigDecimal.ZERO
        } else {
            position.costAvgPrice = position.holdCost!!.divide(position.holdGrams, 8, RoundingMode.HALF_UP)
        }
        position.cumulativeProfit = (position.cumulativeProfit ?: BigDecimal.ZERO).add(realized).setScale(16, RoundingMode.HALF_UP)
        position.cumulativeSellFee = (position.cumulativeSellFee ?: BigDecimal.ZERO).add(sellFee).setScale(16, RoundingMode.HALF_UP)
        position.lastSellTime = LocalDateTime.now()
        updateById(position)
        return GoldPositionApplyResult(
            costAvgBefore = before,
            costAvgAfter = position.costAvgPrice!!,
            sellCost = sellCost,
            realizedProfit = realized,
            position = position,
        )
    }

    override fun refreshValuation(position: AppGoldPosition, latestPrice: BigDecimal, prevClose: BigDecimal?) {
        val lastEval = position.lastEvaluateTime
        if (lastEval != null && Duration.between(lastEval, LocalDateTime.now()).seconds < EVALUATE_MIN_INTERVAL_SEC) {
            return
        }
        val grams = position.holdGrams ?: BigDecimal.ZERO
        val mv = grams.multiply(latestPrice).setScale(16, RoundingMode.HALF_UP)
        val cost = position.holdCost ?: BigDecimal.ZERO
        position.lastMarketValue = mv
        position.lastHoldingProfit = mv.subtract(cost).setScale(16, RoundingMode.HALF_UP)
        val today = LocalDate.now()
        if (position.todayProfitDate != today) {
            position.todayProfitDate = today
            position.todayProfit = BigDecimal.ZERO
        }
        if (prevClose != null && prevClose.signum() > 0) {
            position.todayProfit = grams.multiply(latestPrice.subtract(prevClose)).setScale(16, RoundingMode.HALF_UP)
        }
        position.lastEvaluateTime = LocalDateTime.now()
        updateById(position)
    }

    override fun summaryForUser(userId: Long): GoldHoldingSummaryVo {
        val positions = listUserPositions(userId)
        val items = positions.mapNotNull { pos ->
            val ch = channelService.getById(pos.channelId!!) ?: return@mapNotNull null
            val quote = quoteService.getRealtime(pos.channelId!!)
            val price = quote?.price ?: pos.costAvgPrice ?: BigDecimal.ZERO
            refreshValuation(pos, price, quote?.prevClosePrice)
            buildDetailVo(ch, pos, quote, price)
        }
        goldWalletService.refreshAggregate(userId, items.firstOrNull()?.currencyCode ?: "HKD")
        val z = BigDecimal.ZERO
        return GoldHoldingSummaryVo(
            totalHoldGrams = items.fold(z) { a, x -> a.add(x.holdGrams) },
            totalHoldValue = items.fold(z) { a, x -> a.add(x.holdValue) },
            totalHoldingProfit = items.fold(z) { a, x -> a.add(x.holdingProfit) },
            totalCumulativeProfit = items.fold(z) { a, x -> a.add(x.cumulativeProfit) },
            currencyCode = items.firstOrNull()?.currencyCode ?: "HKD",
            items = items,
        )
    }

    override fun detailForUser(userId: Long, channelId: Long): GoldPositionDetailVo? {
        val pos = findUserChannelPosition(userId, channelId) ?: return null
        val ch = channelService.getById(channelId) ?: return null
        val quote = quoteService.getRealtime(channelId)
        val price = quote?.price ?: pos.costAvgPrice ?: BigDecimal.ZERO
        refreshValuation(pos, price, quote?.prevClosePrice)
        return buildDetailVo(ch, pos, quote, price)
    }

    override fun managePage(req: GoldPositionPageReq): Page<AppGoldPosition> {
        val page = Page<AppGoldPosition>(req.current, req.size)
        val w = KtQueryWrapper(AppGoldPosition()).orderByDesc(AppGoldPosition::createTime)
        req.userId?.let { w.eq(AppGoldPosition::userId, it) }
        req.channelId?.let { w.eq(AppGoldPosition::channelId, it) }
        return page(page, w)
    }

    private fun buildDetailVo(
        ch: AppGoldChannel,
        pos: AppGoldPosition,
        quote: AppGoldPriceQuote?,
        price: BigDecimal,
    ): GoldPositionDetailVo {
        val grams = pos.holdGrams ?: BigDecimal.ZERO
        return GoldPositionDetailVo(
            channelId = ch.id!!,
            channelCode = ch.channelCode ?: "",
            channelName = ch.channelName ?: "",
            accountLabel = pos.accountLabelSnapshot ?: ch.accountLabel,
            accountTag = ch.accountTag,
            logoUrl = ch.logoUrl,
            csLink = ch.csLink,
            currencyCode = pos.currencyCode ?: "HKD",
            holdGrams = grams,
            holdValue = pos.lastMarketValue ?: grams.multiply(price).setScale(16, RoundingMode.HALF_UP),
            costAvgPrice = pos.costAvgPrice ?: BigDecimal.ZERO,
            holdingProfit = pos.lastHoldingProfit ?: BigDecimal.ZERO,
            cumulativeProfit = pos.cumulativeProfit ?: BigDecimal.ZERO,
            todayProfit = pos.todayProfit ?: BigDecimal.ZERO,
            price = price,
            changeAmount = quote?.changeAmount ?: BigDecimal.ZERO,
            changePct = quote?.changePct ?: BigDecimal.ZERO,
            tradingStatus = quote?.tradingStatus ?: 0,
            intradayHigh = quote?.intradayHigh,
            intradayLow = quote?.intradayLow,
            gramScale = ch.gramScale ?: 4,
        )
    }
}
