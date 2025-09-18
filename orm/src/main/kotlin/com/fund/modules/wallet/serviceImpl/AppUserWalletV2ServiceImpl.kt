package com.fund.modules.wallet.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.common.Constants.MARKET_COIN_MAP
import com.fund.exception.BusinessException
import com.fund.modules.wallet.mapper.AppUserWalletV2Mapper
import com.fund.modules.wallet.model.AppUserWalletV2
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.modules.wallet.service.AppWalletOperationLogService
import com.fund.utils.GeneratorIdUtil.generateId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
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

    override fun createWallet(userId: Long, topUserId: Long?, walletType: Int , currencyCode: String): AppUserWalletV2 {
        val wallet = AppUserWalletV2().apply {
            this.userId = userId
            this.topUserId = topUserId
            this.walletType = walletType
            this.currencyCode = currencyCode
            this.availableBalance = BigDecimal.ZERO
            this.frozenBalance = BigDecimal.ZERO
            this.totalBalance = BigDecimal.ZERO
            this.creditScore = 100
            this.status = 1
            this.version = 0
        }
        
        if (!this.save(wallet)) {
            throw BusinessException("创建钱包失败")
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

    @Transactional(rollbackFor = [Exception::class])
    override fun addAvailableBalance(userId: Long, walletType: Int, amount: BigDecimal, operationType: String, remark: String?): Boolean {
        val wallet = findWalletByUserAndType(userId, walletType) 
            ?: throw BusinessException("钱包不存在")
        
        val beforeBalance = wallet.availableBalance ?: BigDecimal.ZERO
        val afterBalance = beforeBalance.add(amount)
        
        // 更新余额
        wallet.availableBalance = afterBalance
        wallet.totalBalance = afterBalance.add(wallet.frozenBalance ?: BigDecimal.ZERO)
        
        if (!this.updateById(wallet)) {
            throw BusinessException("更新余额失败")
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
            remark = remark
        )
        
        return true
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun subtractAvailableBalance(userId: Long, walletType: Int, amount: BigDecimal, operationType: String, remark: String?): Boolean {
        val wallet = findWalletByUserAndType(userId, walletType) 
            ?: throw BusinessException("钱包不存在")
        
        val beforeBalance = wallet.availableBalance ?: BigDecimal.ZERO
        if (beforeBalance.compareTo(amount) < 0) {
            throw BusinessException("余额不足")
        }
        
        val afterBalance = beforeBalance.subtract(amount)
        
        // 更新余额
        wallet.availableBalance = afterBalance
        wallet.totalBalance = afterBalance.add(wallet.frozenBalance ?: BigDecimal.ZERO)
        
        if (!this.updateById(wallet)) {
            throw BusinessException("更新余额失败")
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
            remark = remark
        )
        
        return true
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun freezeBalance(userId: Long, walletType: Int, amount: BigDecimal, operationType: String, remark: String?): Boolean {
        val wallet = findWalletByUserAndType(userId, walletType) 
            ?: throw BusinessException("钱包不存在")
        
        val availableBalance = wallet.availableBalance ?: BigDecimal.ZERO
        val frozenBalance = wallet.frozenBalance ?: BigDecimal.ZERO
        
        if (availableBalance.compareTo(amount) < 0) {
            throw BusinessException("可用余额不足")
        }
        
        val newAvailableBalance = availableBalance.subtract(amount)
        val newFrozenBalance = frozenBalance.add(amount)
        
        // 更新余额
        wallet.availableBalance = newAvailableBalance
        wallet.frozenBalance = newFrozenBalance
        wallet.totalBalance = newAvailableBalance.add(newFrozenBalance)
        
        if (!this.updateById(wallet)) {
            throw BusinessException("冻结余额失败")
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
            remark = remark
        )
        
        return true
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun unfreezeBalance(userId: Long, walletType: Int, amount: BigDecimal, operationType: String, remark: String?): Boolean {
        val wallet = findWalletByUserAndType(userId, walletType) 
            ?: throw BusinessException("钱包不存在")
        
        val availableBalance = wallet.availableBalance ?: BigDecimal.ZERO
        val frozenBalance = wallet.frozenBalance ?: BigDecimal.ZERO
        
        if (frozenBalance.compareTo(amount) < 0) {
            throw BusinessException("冻结余额不足")
        }
        
        val newAvailableBalance = availableBalance.add(amount)
        val newFrozenBalance = frozenBalance.subtract(amount)
        
        // 更新余额
        wallet.availableBalance = newAvailableBalance
        wallet.frozenBalance = newFrozenBalance
        wallet.totalBalance = newAvailableBalance.add(newFrozenBalance)
        
        if (!this.updateById(wallet)) {
            throw BusinessException("解冻余额失败")
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
            remark = remark
        )
        
        return true
    }

    override fun checkBalanceSufficient(userId: Long, walletType: Int, amount: BigDecimal): Boolean {
        val wallet = findWalletByUserAndType(userId, walletType) ?: return false
        val availableBalance = wallet.availableBalance ?: BigDecimal.ZERO
        return availableBalance.compareTo(amount) >= 0
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
