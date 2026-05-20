package com.fund.modules.gold.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.common.RedisKeys
import com.fund.exception.BusinessException
import com.fund.modules.gold.GoldOrderDirection
import com.fund.modules.gold.GoldOrderStatus
import com.fund.modules.gold.mapper.AppGoldOrderMapper
import com.fund.modules.gold.model.AppGoldChannel
import com.fund.modules.gold.model.AppGoldGlobalConfig
import com.fund.modules.gold.model.AppGoldOrder
import com.fund.modules.gold.request.GoldBuyReq
import com.fund.modules.gold.request.GoldOrderPageReq
import com.fund.modules.gold.request.GoldSellReq
import com.fund.modules.gold.service.AppGoldChannelService
import com.fund.modules.gold.service.AppGoldGlobalConfigService
import com.fund.modules.gold.service.AppGoldOrderService
import com.fund.modules.gold.service.AppGoldPositionService
import com.fund.modules.gold.service.AppGoldPriceQuoteService
import com.fund.modules.gold.service.AppUserGoldWalletService
import com.fund.modules.wallet.enum.GoldChangeEnum
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.GeneratorIdUtil
import com.fund.utils.RedisLockService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
open class AppGoldOrderServiceImpl(
    private val walletService: AppUserWalletV2Service,
    private val goldWalletService: AppUserGoldWalletService,
    private val channelService: AppGoldChannelService,
    private val quoteService: AppGoldPriceQuoteService,
    private val positionService: AppGoldPositionService,
    private val globalConfigService: AppGoldGlobalConfigService,
) : ServiceImpl<AppGoldOrderMapper, AppGoldOrder>(),
    AppGoldOrderService {

    override fun userBuy(userId: Long, req: GoldBuyReq): AppGoldOrder {
        val lockKey = RedisKeys.LOCK_GOLD_TRADE + userId + ":" + req.channelId
        return RedisLockService.lockTransaction(lockKey) {
            val cfg = globalConfigService.loadOrCreate()
            if ((cfg.entryEnable ?: 1) != 1) throw BusinessException("积存金入口已关闭")
            val currency = cfg.currencyCode ?: "HKD"
            val channel = channelService.getEnabledById(req.channelId)
                ?: throw BusinessException("渠道不可用")
            val quote = quoteService.getRealtime(req.channelId)
                ?: throw BusinessException("行情未就绪")
            if ((quote.tradingStatus ?: 0) != 1) throw BusinessException("当前非交易时段")
            val price = quote.price ?: throw BusinessException("行情价缺失")

            checkPriceTolerance(req.expectPrice, price, channel, cfg)

            val amount = req.amount.setScale(16, RoundingMode.HALF_UP)
            val minBuy = nonZeroOrFallback(channel.minBuyAmount, cfg.defaultMinBuyAmount)
            if (minBuy.signum() > 0 && amount < minBuy) throw BusinessException("买入金额低于最低限额")

            val gramScale = channel.gramScale ?: cfg.defaultGramScale ?: 4
            val grams = amount.divide(price, gramScale, RoundingMode.HALF_UP)
            if (grams.signum() <= 0) throw BusinessException("买入克数计算为0，金额过小或价格异常")
            val feeRate = nonZeroOrFallback(channel.buyFeeRate, cfg.defaultBuyFeeRate)
            val fee = amount.multiply(feeRate).setScale(16, RoundingMode.HALF_UP)

            val cash = walletService.findWalletByUserAndType(userId, 0, currency)
                ?: walletService.createWallet(userId, null, 0, currency)
            val gold = goldWalletService.ensureWallet(userId, cash.topUserId, currency)

            val order = AppGoldOrder().apply {
                orderNo = "GAB${GeneratorIdUtil.generateId()}"
                this.userId = userId
                cashWalletId = cash.id
                goldWalletId = gold.id
                channelId = channel.id
                channelCode = channel.channelCode
                currencyCode = currency
                channelNameSnapshot = channel.channelName
                accountLabelSnapshot = channel.accountLabel
                direction = GoldOrderDirection.BUY
                this.price = price
                this.expectPrice = req.expectPrice
                this.grams = grams
                this.amount = amount
                this.feeRate = feeRate
                this.feeAmount = fee
                this.walletChangeAmount = amount.add(fee).negate()
                this.quoteId = quote.id
                this.status = GoldOrderStatus.PROCESSING
                this.remark = req.remark
            }
            if (!save(order)) throw BusinessException("订单保存失败")

            walletService.subtractAvailableBalance(
                userId = userId,
                walletType = 0,
                currencyCode = currency,
                amount = amount,
                operationType = GoldChangeEnum.GOLD_ACC_BUY,
                remark = "积存金买入,渠道:${channel.channelName},单号:${order.orderNo}",
            )
            if (fee.signum() > 0) {
                walletService.subtractAvailableBalance(
                    userId = userId,
                    walletType = 0,
                    currencyCode = currency,
                    amount = fee,
                    operationType = GoldChangeEnum.GOLD_ACC_BUY_FEE,
                    remark = "积存金买入手续费,渠道:${channel.channelName},单号:${order.orderNo}",
                )
            }

            val applied = positionService.applyBuy(
                userId = userId,
                cashWalletId = cash.id!!,
                goldWalletId = gold.id!!,
                channel = channel,
                grams = grams,
                principal = amount,
                buyFee = fee,
            )

            goldWalletService.applyBuyStats(gold, grams, amount, fee)

            order.costAvgPriceBefore = applied.costAvgBefore
            order.costAvgPriceAfter = applied.costAvgAfter
            order.status = GoldOrderStatus.FINISHED
            order.finishTime = LocalDateTime.now()
            updateById(order)
            order
        }
    }

    override fun userSell(userId: Long, req: GoldSellReq): AppGoldOrder {
        val lockKey = RedisKeys.LOCK_GOLD_TRADE + userId + ":" + req.channelId
        return RedisLockService.lockTransaction(lockKey) {
            val cfg = globalConfigService.loadOrCreate()
            if ((cfg.entryEnable ?: 1) != 1) throw BusinessException("积存金入口已关闭")
            val currency = cfg.currencyCode ?: "HKD"
            val channel = channelService.getEnabledById(req.channelId)
                ?: throw BusinessException("渠道不可用")
            val quote = quoteService.getRealtime(req.channelId)
                ?: throw BusinessException("行情未就绪")
            if ((quote.tradingStatus ?: 0) != 1) throw BusinessException("当前非交易时段")
            val price = quote.price ?: throw BusinessException("行情价缺失")

            checkPriceTolerance(req.expectPrice, price, channel, cfg)

            val sellGrams = req.grams.setScale(16, RoundingMode.HALF_UP)
            val minSell = nonZeroOrFallback(channel.minSellGrams, cfg.defaultMinSellGrams)
            if (minSell.signum() > 0 && sellGrams < minSell) throw BusinessException("卖出克数低于最低限额")

            val pos = positionService.findUserChannelPosition(userId, channel.id!!)
                ?: throw BusinessException("无持仓")
            val holdGrams = pos.holdGrams ?: BigDecimal.ZERO
            if (sellGrams > holdGrams) throw BusinessException("卖出克数超过持仓")

            val amount = sellGrams.multiply(price).setScale(16, RoundingMode.HALF_UP)
            val feeRate = nonZeroOrFallback(channel.sellFeeRate, cfg.defaultSellFeeRate)
            val fee = amount.multiply(feeRate).setScale(16, RoundingMode.HALF_UP)
            val net = amount.subtract(fee).setScale(16, RoundingMode.HALF_UP)
            if (net.signum() < 0) throw BusinessException("卖出净回款为负，无法落账")

            val cash = walletService.findWalletByUserAndType(userId, 0, currency)
                ?: throw BusinessException("现金钱包不存在")
            val gold = goldWalletService.getByUser(userId, currency)
                ?: throw BusinessException("积存金钱包不存在")

            val order = AppGoldOrder().apply {
                orderNo = "GAS${GeneratorIdUtil.generateId()}"
                this.userId = userId
                cashWalletId = cash.id
                goldWalletId = gold.id
                channelId = channel.id
                channelCode = channel.channelCode
                currencyCode = currency
                channelNameSnapshot = channel.channelName
                accountLabelSnapshot = channel.accountLabel
                direction = GoldOrderDirection.SELL
                this.price = price
                this.expectPrice = req.expectPrice
                this.grams = sellGrams
                this.amount = amount
                this.feeRate = feeRate
                this.feeAmount = fee
                this.walletChangeAmount = net
                this.quoteId = quote.id
                this.status = GoldOrderStatus.PROCESSING
                this.remark = req.remark
            }
            if (!save(order)) throw BusinessException("订单保存失败")

            walletService.addAvailableBalance(
                userId = userId,
                walletType = 0,
                currencyCode = currency,
                amount = amount,
                operationType = GoldChangeEnum.GOLD_ACC_SELL,
                remark = "积存金卖出,渠道:${channel.channelName},单号:${order.orderNo}",
            )
            if (fee.signum() > 0) {
                walletService.subtractAvailableBalance(
                    userId = userId,
                    walletType = 0,
                    currencyCode = currency,
                    amount = fee,
                    operationType = GoldChangeEnum.GOLD_ACC_SELL_FEE,
                    remark = "积存金卖出手续费,渠道:${channel.channelName},单号:${order.orderNo}",
                )
            }

            val applied = positionService.applySell(pos, sellGrams, price, fee)

            goldWalletService.applySellStats(
                wallet = gold,
                sellGrams = sellGrams,
                sellCost = applied.sellCost,
                realizedProfit = applied.realizedProfit,
                sellFee = fee,
            )

            order.costAvgPriceBefore = applied.costAvgBefore
            order.costAvgPriceAfter = applied.costAvgAfter
            order.realizedProfit = applied.realizedProfit
            order.realizedProfitNet = applied.realizedProfit.subtract(fee).setScale(16, RoundingMode.HALF_UP)
            order.status = GoldOrderStatus.FINISHED
            order.finishTime = LocalDateTime.now()
            updateById(order)
            order
        }
    }

    override fun pageMyOrders(userId: Long, req: GoldOrderPageReq): Page<AppGoldOrder> {
        val page = Page<AppGoldOrder>(req.current, req.size)
        val w = KtQueryWrapper(AppGoldOrder())
            .eq(AppGoldOrder::userId, userId)
            .eq(AppGoldOrder::status, GoldOrderStatus.FINISHED)
            .orderByDesc(AppGoldOrder::createTime)
        req.channelId?.let { w.eq(AppGoldOrder::channelId, it) }
        req.direction?.let { w.eq(AppGoldOrder::direction, it) }
        req.startTime?.let { w.ge(AppGoldOrder::createTime, it) }
        req.endTime?.let { w.le(AppGoldOrder::createTime, it) }
        return page(page, w)
    }

    override fun managePage(req: GoldOrderPageReq): Page<AppGoldOrder> {
        val page = Page<AppGoldOrder>(req.current, req.size)
        val w = KtQueryWrapper(AppGoldOrder()).orderByDesc(AppGoldOrder::createTime)
        req.userId?.let { w.eq(AppGoldOrder::userId, it) }
        req.channelId?.let { w.eq(AppGoldOrder::channelId, it) }
        req.direction?.let { w.eq(AppGoldOrder::direction, it) }
        req.startTime?.let { w.ge(AppGoldOrder::createTime, it) }
        req.endTime?.let { w.le(AppGoldOrder::createTime, it) }
        return page(page, w)
    }

    private fun checkPriceTolerance(
        expect: BigDecimal?,
        serverPrice: BigDecimal,
        channel: AppGoldChannel,
        cfg: AppGoldGlobalConfig,
    ) {
        if (expect == null || expect.signum() <= 0) return
        val bps = (channel.priceToleranceBps ?: 0).takeIf { it > 0 }
            ?: (cfg.defaultPriceToleranceBps ?: 100)
        val diff = serverPrice.subtract(expect).abs()
        val limit = expect.multiply(BigDecimal(bps)).divide(BigDecimal(10000), 8, RoundingMode.HALF_UP)
        if (diff.compareTo(limit) > 0) {
            throw BusinessException("价格波动较大，请确认最新价后重试")
        }
    }

    private fun nonZeroOrFallback(channelValue: BigDecimal?, fallback: BigDecimal?): BigDecimal {
        val ch = channelValue ?: BigDecimal.ZERO
        return if (ch.signum() > 0) ch else (fallback ?: BigDecimal.ZERO)
    }
}
