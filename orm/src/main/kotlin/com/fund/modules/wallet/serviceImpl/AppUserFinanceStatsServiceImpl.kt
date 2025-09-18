package com.fund.modules.wallet.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.exception.BusinessException
import com.fund.modules.wallet.mapper.AppUserFinanceStatsMapper
import com.fund.modules.wallet.model.AppUserFinanceStats
import com.fund.modules.wallet.service.AppUserFinanceStatsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 用户资金统计表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
@Service
open class AppUserFinanceStatsServiceImpl : ServiceImpl<AppUserFinanceStatsMapper, AppUserFinanceStats>(), AppUserFinanceStatsService {

    override fun createFinanceStats(userId: Long, topUserId: Long?): AppUserFinanceStats {
        val stats = AppUserFinanceStats().apply {
            this.userId = userId
            this.topUserId = topUserId
            this.totalRecharge = BigDecimal.ZERO
            this.totalWithdraw = BigDecimal.ZERO
            this.totalIncome = BigDecimal.ZERO
            this.totalCommission = BigDecimal.ZERO
            this.totalTradingVolume = BigDecimal.ZERO
        }
        
        if (!this.save(stats)) {
            throw BusinessException("创建资金统计失败")
        }
        
        return stats
    }

    override fun findFinanceStatsByUserId(userId: Long): AppUserFinanceStats? {
        return getOne(
            KtQueryWrapper(AppUserFinanceStats())
                .eq(AppUserFinanceStats::userId, userId)
                .last("limit 1")
        )
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun addRechargeStats(userId: Long, amount: BigDecimal): Boolean {
        val stats = findFinanceStatsByUserId(userId) ?: createFinanceStats(userId, null)
        
        val currentTotal = stats.totalRecharge ?: BigDecimal.ZERO
        stats.totalRecharge = currentTotal.add(amount)
        stats.lastRechargeTime = LocalDateTime.now()
        
        return this.updateById(stats)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun addWithdrawStats(userId: Long, amount: BigDecimal): Boolean {
        val stats = findFinanceStatsByUserId(userId) ?: createFinanceStats(userId, null)
        
        val currentTotal = stats.totalWithdraw ?: BigDecimal.ZERO
        stats.totalWithdraw = currentTotal.add(amount)
        stats.lastWithdrawTime = LocalDateTime.now()
        
        return this.updateById(stats)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun addIncomeStats(userId: Long, amount: BigDecimal): Boolean {
        val stats = findFinanceStatsByUserId(userId) ?: createFinanceStats(userId, null)
        
        val currentTotal = stats.totalIncome ?: BigDecimal.ZERO
        stats.totalIncome = currentTotal.add(amount)
        
        return this.updateById(stats)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun addCommissionStats(userId: Long, amount: BigDecimal): Boolean {
        val stats = findFinanceStatsByUserId(userId) ?: createFinanceStats(userId, null)
        
        val currentTotal = stats.totalCommission ?: BigDecimal.ZERO
        stats.totalCommission = currentTotal.add(amount)
        
        return this.updateById(stats)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun addTradingVolumeStats(userId: Long, amount: BigDecimal): Boolean {
        val stats = findFinanceStatsByUserId(userId) ?: createFinanceStats(userId, null)
        
        val currentTotal = stats.totalTradingVolume ?: BigDecimal.ZERO
        stats.totalTradingVolume = currentTotal.add(amount)
        
        return this.updateById(stats)
    }
}
