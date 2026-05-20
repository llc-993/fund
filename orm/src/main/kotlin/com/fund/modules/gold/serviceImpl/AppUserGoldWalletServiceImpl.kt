package com.fund.modules.gold.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.exception.BusinessException
import com.fund.modules.gold.mapper.AppGoldPositionMapper
import com.fund.modules.gold.mapper.AppUserGoldWalletMapper
import com.fund.modules.gold.model.AppGoldPosition
import com.fund.modules.gold.model.AppUserGoldWallet
import com.fund.modules.gold.service.AppUserGoldWalletService
import com.fund.modules.wallet.service.AppUserWalletV2Service
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
open class AppUserGoldWalletServiceImpl(
    private val walletService: AppUserWalletV2Service,
    @Lazy private val positionMapper: AppGoldPositionMapper,
) : ServiceImpl<AppUserGoldWalletMapper, AppUserGoldWallet>(),
    AppUserGoldWalletService {

    override fun ensureWallet(userId: Long, topUserId: Long?, currencyCode: String): AppUserGoldWallet {
        getByUser(userId, currencyCode)?.let { return it }
        val cash = walletService.findWalletByUserAndType(userId, 0, currencyCode)
            ?: walletService.createWallet(userId, topUserId, 0, currencyCode)
        val w = AppUserGoldWallet().apply {
            this.userId = userId
            this.topUserId = topUserId ?: cash.topUserId
            this.walletId = cash.id
            this.currencyCode = currencyCode
            this.totalGrams = BigDecimal.ZERO
            this.totalCost = BigDecimal.ZERO
            this.avgCostPrice = BigDecimal.ZERO
            this.totalInvest = BigDecimal.ZERO
            this.totalRealizedProfit = BigDecimal.ZERO
            this.totalHoldingProfit = BigDecimal.ZERO
            this.totalMarketValue = BigDecimal.ZERO
            this.totalBuyFee = BigDecimal.ZERO
            this.totalSellFee = BigDecimal.ZERO
            this.status = 1
            this.version = 0
        }
        if (!save(w)) throw BusinessException("创建积存金钱包失败")
        return w
    }

    override fun getByUser(userId: Long, currencyCode: String): AppUserGoldWallet? = getOne(
        KtQueryWrapper(AppUserGoldWallet())
            .eq(AppUserGoldWallet::userId, userId)
            .eq(AppUserGoldWallet::currencyCode, currencyCode)
            .last("limit 1"),
    )

    override fun applyBuyStats(
        wallet: AppUserGoldWallet,
        grams: BigDecimal,
        principal: BigDecimal,
        buyFee: BigDecimal,
    ): AppUserGoldWallet {
        if (grams.signum() <= 0 || principal.signum() <= 0) {
            throw BusinessException("买入克数与本金必须大于0")
        }
        wallet.totalGrams = (wallet.totalGrams ?: BigDecimal.ZERO).add(grams).setScale(16, RoundingMode.HALF_UP)
        wallet.totalCost = (wallet.totalCost ?: BigDecimal.ZERO).add(principal).setScale(16, RoundingMode.HALF_UP)
        wallet.avgCostPrice = if (wallet.totalGrams!!.signum() > 0) {
            wallet.totalCost!!.divide(wallet.totalGrams, 8, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }
        wallet.totalInvest = (wallet.totalInvest ?: BigDecimal.ZERO).add(principal).setScale(16, RoundingMode.HALF_UP)
        wallet.totalBuyFee = (wallet.totalBuyFee ?: BigDecimal.ZERO).add(buyFee).setScale(16, RoundingMode.HALF_UP)
        if (!updateById(wallet)) throw BusinessException("积存金钱包更新失败（买入统计）")
        return wallet
    }

    override fun applySellStats(
        wallet: AppUserGoldWallet,
        sellGrams: BigDecimal,
        sellCost: BigDecimal,
        realizedProfit: BigDecimal,
        sellFee: BigDecimal,
    ): AppUserGoldWallet {
        if (sellGrams.signum() <= 0) throw BusinessException("卖出克数必须大于0")
        val nowGrams = (wallet.totalGrams ?: BigDecimal.ZERO).subtract(sellGrams)
        val nowCost = (wallet.totalCost ?: BigDecimal.ZERO).subtract(sellCost)
        wallet.totalGrams = (if (nowGrams.signum() < 0) BigDecimal.ZERO else nowGrams).setScale(16, RoundingMode.HALF_UP)
        wallet.totalCost = (if (nowCost.signum() < 0) BigDecimal.ZERO else nowCost).setScale(16, RoundingMode.HALF_UP)
        wallet.avgCostPrice = if (wallet.totalGrams!!.signum() > 0) {
            wallet.totalCost!!.divide(wallet.totalGrams, 8, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }
        wallet.totalRealizedProfit = (wallet.totalRealizedProfit ?: BigDecimal.ZERO)
            .add(realizedProfit).setScale(16, RoundingMode.HALF_UP)
        wallet.totalSellFee = (wallet.totalSellFee ?: BigDecimal.ZERO)
            .add(sellFee).setScale(16, RoundingMode.HALF_UP)
        if (!updateById(wallet)) throw BusinessException("积存金钱包更新失败（卖出统计）")
        return wallet
    }

    override fun refreshAggregate(userId: Long, currencyCode: String): AppUserGoldWallet? {
        val wallet = getByUser(userId, currencyCode) ?: return null
        val positions = positionMapper.selectList(
            KtQueryWrapper(AppGoldPosition()).eq(AppGoldPosition::userId, userId),
        )
        val z = BigDecimal.ZERO
        wallet.totalMarketValue = positions.fold(z) { a, p -> a.add(p.lastMarketValue ?: z) }
            .setScale(16, RoundingMode.HALF_UP)
        wallet.totalHoldingProfit = positions.fold(z) { a, p -> a.add(p.lastHoldingProfit ?: z) }
            .setScale(16, RoundingMode.HALF_UP)
        wallet.lastEvaluateTime = LocalDateTime.now()
        updateById(wallet)
        return wallet
    }
}
