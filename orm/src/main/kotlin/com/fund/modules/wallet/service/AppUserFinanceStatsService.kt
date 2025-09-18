package com.fund.modules.wallet.service

import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.wallet.model.AppUserFinanceStats
import java.math.BigDecimal

/**
 * <p>
 * 用户资金统计表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
interface AppUserFinanceStatsService : IService<AppUserFinanceStats> {

    /**
     * 创建用户资金统计记录
     * @param userId 用户ID
     * @param topUserId 总代用户ID
     * @return 创建的统计记录
     */
    fun createFinanceStats(userId: Long, topUserId: Long?): AppUserFinanceStats

    /**
     * 根据用户ID查询资金统计
     * @param userId 用户ID
     * @return 资金统计信息
     */
    fun findFinanceStatsByUserId(userId: Long): AppUserFinanceStats?

    /**
     * 增加充值统计
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 是否成功
     */
    fun addRechargeStats(userId: Long, amount: BigDecimal): Boolean

    /**
     * 增加提现统计
     * @param userId 用户ID
     * @param amount 提现金额
     * @return 是否成功
     */
    fun addWithdrawStats(userId: Long, amount: BigDecimal): Boolean

    /**
     * 增加收入统计
     * @param userId 用户ID
     * @param amount 收入金额
     * @return 是否成功
     */
    fun addIncomeStats(userId: Long, amount: BigDecimal): Boolean

    /**
     * 增加佣金统计
     * @param userId 用户ID
     * @param amount 佣金金额
     * @return 是否成功
     */
    fun addCommissionStats(userId: Long, amount: BigDecimal): Boolean

    /**
     * 增加交易量统计
     * @param userId 用户ID
     * @param amount 交易金额
     * @return 是否成功
     */
    fun addTradingVolumeStats(userId: Long, amount: BigDecimal): Boolean
}
