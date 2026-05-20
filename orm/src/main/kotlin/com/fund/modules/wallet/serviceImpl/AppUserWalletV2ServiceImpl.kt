package com.fund.modules.wallet.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.common.Constants.MARKET_COIN_MAP
import com.fund.exception.BusinessException
import com.fund.modules.wallet.enum.GoldChangeEnum
import com.fund.modules.wallet.mapper.AppUserWalletV2Mapper
import com.fund.modules.wallet.model.AppUserWalletV2
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.modules.wallet.service.AppWalletOperationLogService
import com.fund.utils.GeneratorIdUtil.generateId
import com.fund.utils.RedisLockService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

/**
 * <p>
 * 用户钱包表V2 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
@Service
open class AppUserWalletV2ServiceImpl(
    private val appWalletOperationLogService: AppWalletOperationLogService
) : ServiceImpl<AppUserWalletV2Mapper, AppUserWalletV2>(), AppUserWalletV2Service {

    private val logger = KotlinLogging.logger {}
    private val walletLockPrefix = "lock:wallet:v2:"

    private fun walletLockKey(userId: Long, walletType: Int, currencyCode: String): String {
        return "$walletLockPrefix$userId:$walletType:$currencyCode"
    }

    override fun createWallet(userId: Long, topUserId: Long?, walletType: Int , currencyCode: String): AppUserWalletV2 {
        val wallet = AppUserWalletV2().apply {
            this.userId = userId
            this.topUserId = topUserId
            this.walletType = walletType
            this.currencyCode = currencyCode
            this.availableBalance = BigDecimal.ZERO
            this.frozenBalance = BigDecimal.ZERO
            this.totalBalance = BigDecimal.ZERO
            this.aiQuantFreeze = BigDecimal.ZERO
            this.aiQuantTotalInvest = BigDecimal.ZERO
            this.aiQuantTotalProfit = BigDecimal.ZERO
            this.aiQuantTotalFee = BigDecimal.ZERO
            this.createTime = LocalDateTime.now()
            this.updateTime = LocalDateTime.now()
            this.creditScore = 100
            this.status = 1
            this.version = 0
        }
        
        if (!this.save(wallet)) {
            throw BusinessException("wallet_create_failed")
        }
        
        return wallet
    }

    override fun findWalletByUserAndType(userId: Long, walletType: Int, currencyCode: String): AppUserWalletV2? {
        return getOne(
            KtQueryWrapper(AppUserWalletV2())
                .eq(AppUserWalletV2::userId, userId)
                .eq(AppUserWalletV2::walletType, walletType)
                .eq(AppUserWalletV2::currencyCode, currencyCode)
                .last("limit 1")
        )
    }

    override fun addAvailableBalance(userId: Long, walletType: Int, currencyCode: String, amount: BigDecimal, operationType: GoldChangeEnum, remark: String?): Boolean {
        val lockKey = walletLockKey(userId, walletType, currencyCode)
        return RedisLockService.lockTransaction(lockKey) {
            val wallet = findWalletByUserAndType(userId, walletType, currencyCode)
                ?: throw BusinessException("wallet_not_found")

            val beforeBalance = wallet.availableBalance ?: BigDecimal.ZERO
            val afterBalance = beforeBalance.add(amount)

            // 更新余额
            wallet.availableBalance = afterBalance
            wallet.totalBalance = afterBalance.add(wallet.frozenBalance ?: BigDecimal.ZERO)

            if (!this.updateById(wallet)) {
                throw BusinessException("wallet_update_balance_failed")
            }

            // 记录操作日志
            appWalletOperationLogService.logOperation(
                userId = userId,
                walletType = walletType,
                operationType = operationType,
                amount = amount,
                beforeBalance = beforeBalance,
                afterBalance = afterBalance,
                status = 1,
                remark = remark + """
                    ,币种:$currencyCode
                """.trimIndent()
            )

            true
        }
    }

    override fun subtractAvailableBalance(userId: Long, walletType: Int, currencyCode: String, amount: BigDecimal, operationType: GoldChangeEnum, remark: String?): Boolean {
        val lockKey = walletLockKey(userId, walletType, currencyCode)
        return RedisLockService.lockTransaction(lockKey) {
            val wallet = findWalletByUserAndType(userId, walletType, currencyCode)
                ?: throw BusinessException("wallet_not_found")

            val beforeBalance = wallet.availableBalance ?: BigDecimal.ZERO
            if (beforeBalance.compareTo(amount) < 0) {
                throw BusinessException("wallet_insufficient_balance")
            }

            val afterBalance = beforeBalance.subtract(amount)

            // 更新余额
            wallet.availableBalance = afterBalance
            wallet.totalBalance = afterBalance.add(wallet.frozenBalance ?: BigDecimal.ZERO)

            if (!this.updateById(wallet)) {
                throw BusinessException("wallet_update_balance_failed")
            }

            // 记录操作日志
            appWalletOperationLogService.logOperation(
                userId = userId,
                walletType = walletType,
                operationType = operationType,
                amount = amount.negate(),
                beforeBalance = beforeBalance,
                afterBalance = afterBalance,
                status = 1,
                remark = remark + """
                    ,币种:$currencyCode
                """.trimIndent()
            )

            true
        }
    }

    override fun freezeBalance(userId: Long, walletType: Int, currencyCode: String, amount: BigDecimal, operationType: GoldChangeEnum, remark: String?): Boolean {
        val lockKey = walletLockKey(userId, walletType, currencyCode)
        return RedisLockService.lockTransaction(lockKey) {
            val wallet = findWalletByUserAndType(userId, walletType, currencyCode)
                ?: throw BusinessException("wallet_not_found")

            val availableBalance = wallet.availableBalance ?: BigDecimal.ZERO
            val frozenBalance = wallet.frozenBalance ?: BigDecimal.ZERO

            if (availableBalance.compareTo(amount) < 0) {
                throw BusinessException("wallet_insufficient_available_balance")
            }

            val newAvailableBalance = availableBalance.subtract(amount)
            val newFrozenBalance = frozenBalance.add(amount)

            // 更新余额
            wallet.availableBalance = newAvailableBalance
            wallet.frozenBalance = newFrozenBalance
            wallet.totalBalance = newAvailableBalance.add(newFrozenBalance)

            if (!this.updateById(wallet)) {
                throw BusinessException("wallet_freeze_failed")
            }

            // 记录操作日志
            appWalletOperationLogService.logOperation(
                userId = userId,
                walletType = walletType,
                operationType = operationType,
                amount = amount,
                beforeBalance = availableBalance,
                afterBalance = newAvailableBalance,
                status = 1,
                remark = remark + """
                    ,币种:$currencyCode
                """.trimIndent()
            )

            true
        }
    }

    override fun unfreezeBalance(userId: Long, walletType: Int, currencyCode: String, amount: BigDecimal, operationType: GoldChangeEnum, remark: String?): Boolean {
        val lockKey = walletLockKey(userId, walletType, currencyCode)
        return RedisLockService.lockTransaction(lockKey) {
            val wallet = findWalletByUserAndType(userId, walletType, currencyCode)
                ?: throw BusinessException("wallet_not_found")

            val availableBalance = wallet.availableBalance ?: BigDecimal.ZERO
            val frozenBalance = wallet.frozenBalance ?: BigDecimal.ZERO

            if (frozenBalance.compareTo(amount) < 0) {
                throw BusinessException("wallet_insufficient_freeze_balance")
            }

            val newAvailableBalance = availableBalance.add(amount)
            val newFrozenBalance = frozenBalance.subtract(amount)

            // 更新余额
            wallet.availableBalance = newAvailableBalance
            wallet.frozenBalance = newFrozenBalance
            wallet.totalBalance = newAvailableBalance.add(newFrozenBalance)

            if (!this.updateById(wallet)) {
                throw BusinessException("wallet_unfreeze_failed")
            }

            // 记录操作日志
            appWalletOperationLogService.logOperation(
                userId = userId,
                walletType = walletType,
                operationType = operationType,
                amount = amount,
                beforeBalance = availableBalance,
                afterBalance = newAvailableBalance,
                status = 1,
                remark = remark + """
                    ,币种:$currencyCode
                """.trimIndent()
            )

            true
        }
    }

    override fun checkBalanceSufficient(userId: Long, walletType: Int, currencyCode: String, amount: BigDecimal): Boolean {
        val wallet = findWalletByUserAndType(userId, walletType, currencyCode) ?: return false
        val availableBalance = wallet.availableBalance ?: BigDecimal.ZERO
        return availableBalance.compareTo(amount) >= 0
    }

    override fun freezeAiQuantPrincipal(
        userId: Long,
        walletType: Int,
        currencyCode: String,
        amount: BigDecimal,
        operationType: GoldChangeEnum,
        remark: String?,
        relatedCycleId: Long?
    ): Boolean {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException("wallet_freeze_amount_must_be_positive")
        }
        val wallet = findWalletByUserAndType(userId, walletType, currencyCode)
            ?: throw BusinessException("wallet_not_found")
        val available = wallet.availableBalance ?: BigDecimal.ZERO
        if (available.compareTo(amount) < 0) {
            throw BusinessException("wallet_insufficient_available_balance")
        }
        val freezeBefore = wallet.aiQuantFreeze ?: BigDecimal.ZERO
        val newAvailable = available.subtract(amount).setScale(16, RoundingMode.HALF_UP)
        val newAiFreeze = freezeBefore.add(amount).setScale(16, RoundingMode.HALF_UP)
        wallet.availableBalance = newAvailable
        wallet.aiQuantFreeze = newAiFreeze
        wallet.totalBalance = newAvailable.add(wallet.frozenBalance ?: BigDecimal.ZERO)
        if (!updateById(wallet)) {
            throw BusinessException("ai_quant_freeze_failed")
        }
        appWalletOperationLogService.logOperation(
            userId = userId,
            walletType = walletType,
            operationType = operationType,
            amount = amount.negate(),
            beforeBalance = available,
            afterBalance = newAvailable,
            relatedId = relatedCycleId,
            relatedType = "ai_quant_cycle",
            status = 1,
            remark = (remark ?: "") + ",币种:$currencyCode,ai_quant_freeze:$freezeBefore->$newAiFreeze"
        )
        return true
    }

    override fun releaseAiQuantPrincipal(
        userId: Long,
        walletType: Int,
        currencyCode: String,
        amount: BigDecimal,
        operationType: GoldChangeEnum,
        remark: String?,
        relatedCycleId: Long?
    ): Boolean {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException("wallet_release_amount_must_be_positive")
        }
        val wallet = findWalletByUserAndType(userId, walletType, currencyCode)
            ?: throw BusinessException("wallet_not_found")
        val available = wallet.availableBalance ?: BigDecimal.ZERO
        val freeze = wallet.aiQuantFreeze ?: BigDecimal.ZERO
        if (freeze.compareTo(amount) < 0) {
            throw BusinessException("ai_quant_insufficient_freeze_principal")
        }
        val newAvailable = available.add(amount).setScale(16, RoundingMode.HALF_UP)
        val newFreeze = freeze.subtract(amount).setScale(16, RoundingMode.HALF_UP)
        wallet.availableBalance = newAvailable
        wallet.aiQuantFreeze = newFreeze
        wallet.totalBalance = newAvailable.add(wallet.frozenBalance ?: BigDecimal.ZERO)
        if (!updateById(wallet)) {
            throw BusinessException("ai_quant_release_failed")
        }
        appWalletOperationLogService.logOperation(
            userId = userId,
            walletType = walletType,
            operationType = operationType,
            amount = amount,
            beforeBalance = available,
            afterBalance = newAvailable,
            relatedId = relatedCycleId,
            relatedType = "ai_quant_cycle",
            status = 1,
            remark = (remark ?: "") + ",币种:$currencyCode,ai_quant_freeze:$freeze->$newFreeze"
        )
        return true
    }

    override fun settleAiQuantProfit(
        userId: Long,
        walletType: Int,
        currencyCode: String,
        amount: BigDecimal,
        operationType: GoldChangeEnum,
        remark: String?,
        relatedCycleId: Long?
    ): Boolean {
        val wallet = findWalletByUserAndType(userId, walletType, currencyCode)
            ?: throw BusinessException("wallet_not_found")
        val available = wallet.availableBalance ?: BigDecimal.ZERO
        val newAvailable = available.add(amount).setScale(16, RoundingMode.HALF_UP)
        if (newAvailable.compareTo(BigDecimal.ZERO) < 0) {
            throw BusinessException("wallet_insufficient_available_balance")
        }
        wallet.availableBalance = newAvailable
        wallet.totalBalance = newAvailable.add(wallet.frozenBalance ?: BigDecimal.ZERO)
        if (!updateById(wallet)) {
            throw BusinessException("ai_quant_settle_failed")
        }
        appWalletOperationLogService.logOperation(
            userId = userId,
            walletType = walletType,
            operationType = operationType,
            amount = amount,
            beforeBalance = available,
            afterBalance = newAvailable,
            relatedId = relatedCycleId,
            relatedType = "ai_quant_cycle",
            status = 1,
            remark = (remark ?: "") + ",币种:$currencyCode"
        )
        return true
    }

    override fun accumulateAiQuantStats(
        userId: Long,
        walletType: Int,
        currencyCode: String,
        netProfitDelta: BigDecimal,
        feeDelta: BigDecimal,
        investDelta: BigDecimal,
    ): Boolean {
        val wallet = findWalletByUserAndType(userId, walletType, currencyCode)
            ?: throw BusinessException("wallet_not_found")
        val profitWas = wallet.aiQuantTotalProfit ?: BigDecimal.ZERO
        val feeWas = wallet.aiQuantTotalFee ?: BigDecimal.ZERO
        wallet.aiQuantTotalProfit = profitWas.add(netProfitDelta).setScale(16, RoundingMode.HALF_UP)
        wallet.aiQuantTotalFee = feeWas.add(feeDelta).setScale(16, RoundingMode.HALF_UP)
        if (investDelta.signum() != 0) {
            val investWas = wallet.aiQuantTotalInvest ?: BigDecimal.ZERO
            wallet.aiQuantTotalInvest = investWas.add(investDelta).setScale(16, RoundingMode.HALF_UP)
        }
        if (!updateById(wallet)) {
            throw BusinessException("ai_quant_stats_update_failed")
        }
        return true
    }

    override fun getCoinByStockFlag(stockFlag: String?): String {
        return when (stockFlag?.uppercase()) {
            "US" -> "USD"
            "CN" -> "CNY"
            "HK" -> "HKD"
            "IN" -> "INR"
            "DE" -> "EUR"
            "JP" -> "JPY"
            "GB" -> "GBP"
            "AU" -> "AUD"
            "CA" -> "CAD"
            "CH" -> "CHF"
            "SG" -> "SGD"
            "KR" -> "KRW"
            "TH" -> "THB"
            "MY" -> "MYR"
            "PH" -> "PHP"
            "ID" -> "IDR"
            "VN" -> "VND"
            "TW" -> "TWD"
            else -> {
                logger.warn("未知的股票市场标志: $stockFlag，使用默认币种 USD")
                MARKET_COIN_MAP[stockFlag?.uppercase()] ?: "USD"
            }
        }
    }
}
