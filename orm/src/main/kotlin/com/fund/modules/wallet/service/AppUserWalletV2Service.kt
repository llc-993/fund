package com.fund.modules.wallet.service

import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.wallet.enum.GoldChangeEnum
import com.fund.modules.wallet.model.AppUserWalletV2
import java.math.BigDecimal

/**
 * <p>
 * 用户钱包表V2 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
interface AppUserWalletV2Service : IService<AppUserWalletV2> {

    /**
     * 创建用户钱包
     * @param userId 用户ID
     * @param topUserId 总代用户ID
     * @param walletType 钱包类型
     * @param currencyCode 币种代码
     * @return 创建的钱包
     */
    fun createWallet(userId: Long, topUserId: Long?, walletType: Int = 0, currencyCode: String = "HKD"): AppUserWalletV2

    /**
     * 根据用户ID和钱包类型查询钱包
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param currencyCode 币种代码
     * @return 钱包信息
     */
    fun findWalletByUserAndType(userId: Long, walletType: Int = 0, currencyCode: String = "HKD"): AppUserWalletV2?

    /**
     * 增加可用余额
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param currencyCode 币种代码
     * @param amount 金额
     * @param operationType 操作类型
     * @param remark 备注
     * @return 是否成功
     */
    fun addAvailableBalance(userId: Long, walletType: Int = 0, currencyCode: String = "HKD", amount: BigDecimal, operationType: GoldChangeEnum, remark: String? = null): Boolean

    /**
     * 减少可用余额
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param currencyCode 币种代码
     * @param amount 金额
     * @param operationType 操作类型
     * @param remark 备注
     * @return 是否成功
     */
    fun subtractAvailableBalance(userId: Long, walletType: Int = 0, currencyCode: String = "HKD", amount: BigDecimal, operationType: GoldChangeEnum, remark: String? = null): Boolean

    /**
     * 冻结余额
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param currencyCode 币种代码
     * @param amount 金额
     * @param operationType 操作类型
     * @param remark 备注
     * @return 是否成功
     */
    fun freezeBalance(userId: Long, walletType: Int = 0, currencyCode: String = "HKD", amount: BigDecimal, operationType: GoldChangeEnum, remark: String? = null): Boolean

    /**
     * 解冻余额
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param currencyCode 币种代码
     * @param amount 金额
     * @param operationType 操作类型
     * @param remark 备注
     * @return 是否成功
     */
    fun unfreezeBalance(userId: Long, walletType: Int = 0, currencyCode: String = "HKD", amount: BigDecimal, operationType: GoldChangeEnum, remark: String? = null): Boolean

    /**
     * 检查余额是否足够
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param currencyCode 币种代码
     * @param amount 需要金额
     * @return 是否足够
     */
    fun checkBalanceSufficient(userId: Long, walletType: Int = 0, currencyCode: String = "HKD", amount: BigDecimal): Boolean


    /**
     * 根据股票市场获取对应币种
     */
     fun getCoinByStockFlag(stockFlag: String?): String

    /**
     * 冻结 AI 量化本金：available_balance 减少，ai_quant_freeze 增加。
     * `ai_quant_total_invest` 在审核通过时由 `accumulateAiQuantStats(..., investDelta)` 累加。
     * 事务边界由上层 RedisLockService.lockTransaction 包住，此处不加 @Transactional。
     */
    fun freezeAiQuantPrincipal(
        userId: Long,
        walletType: Int = 0,
        currencyCode: String = "HKD",
        amount: BigDecimal,
        operationType: GoldChangeEnum,
        remark: String? = null,
        relatedCycleId: Long? = null
    ): Boolean

    /**
     * 释放 AI 量化冻结本金：available_balance 增加，ai_quant_freeze 减少。
     * 不改变 ai_quant_total_invest。
     */
    fun releaseAiQuantPrincipal(
        userId: Long,
        walletType: Int = 0,
        currencyCode: String = "HKD",
        amount: BigDecimal,
        operationType: GoldChangeEnum,
        remark: String? = null,
        relatedCycleId: Long? = null
    ): Boolean

    /**
     * 结算 AI 量化盈亏（正负均可）：直接增减 available_balance。
     */
    fun settleAiQuantProfit(
        userId: Long,
        walletType: Int = 0,
        currencyCode: String = "HKD",
        amount: BigDecimal,
        operationType: GoldChangeEnum,
        remark: String? = null,
        relatedCycleId: Long? = null
    ): Boolean

    /**
     * 累加 AI 量化统计字段 ai_quant_total_profit / ai_quant_total_fee / ai_quant_total_invest（可选 investDelta，不含可用余额变动）。
     */
    fun accumulateAiQuantStats(
        userId: Long,
        walletType: Int = 0,
        currencyCode: String = "HKD",
        netProfitDelta: BigDecimal,
        feeDelta: BigDecimal,
        investDelta: BigDecimal = BigDecimal.ZERO,
    ): Boolean
}
