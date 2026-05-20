package com.fund.modules.gold.service

import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.gold.model.AppUserGoldWallet
import java.math.BigDecimal

/** 用户积存金汇总账户服务（不直接动现金） */
interface AppUserGoldWalletService : IService<AppUserGoldWallet> {
    /** 确保积存金钱包存在；缺现金钱包时先创建 */
    fun ensureWallet(userId: Long, topUserId: Long?, currencyCode: String = "HKD"): AppUserGoldWallet

    fun getByUser(userId: Long, currencyCode: String = "HKD"): AppUserGoldWallet?

    fun applyBuyStats(
        wallet: AppUserGoldWallet,
        grams: BigDecimal,
        principal: BigDecimal,
        buyFee: BigDecimal,
    ): AppUserGoldWallet

    fun applySellStats(
        wallet: AppUserGoldWallet,
        sellGrams: BigDecimal,
        sellCost: BigDecimal,
        realizedProfit: BigDecimal,
        sellFee: BigDecimal,
    ): AppUserGoldWallet

    /** 按持仓汇总刷新总市值与浮盈 */
    fun refreshAggregate(userId: Long, currencyCode: String = "HKD"): AppUserGoldWallet?
}
