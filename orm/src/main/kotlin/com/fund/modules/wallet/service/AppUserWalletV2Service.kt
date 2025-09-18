package com.fund.modules.wallet.service

import com.baomidou.mybatisplus.extension.service.IService
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
    fun createWallet(userId: Long, topUserId: Long?, walletType: Int = 0, currencyCode: String = "CNY"): AppUserWalletV2

    /**
     * 根据用户ID和钱包类型查询钱包
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param currencyCode 币种代码
     * @return 钱包信息
     */
    fun findWalletByUserAndType(userId: Long, walletType: Int = 0, currencyCode: String = "CNY"): AppUserWalletV2?

    /**
     * 增加可用余额
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param amount 金额
     * @param operationType 操作类型
     * @param remark 备注
     * @return 是否成功
     */
    fun addAvailableBalance(userId: Long, walletType: Int = 0, amount: BigDecimal, operationType: String, remark: String? = null): Boolean

    /**
     * 减少可用余额
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param amount 金额
     * @param operationType 操作类型
     * @param remark 备注
     * @return 是否成功
     */
    fun subtractAvailableBalance(userId: Long, walletType: Int = 0, amount: BigDecimal, operationType: String, remark: String? = null): Boolean

    /**
     * 冻结余额
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param amount 金额
     * @param operationType 操作类型
     * @param remark 备注
     * @return 是否成功
     */
    fun freezeBalance(userId: Long, walletType: Int = 0, amount: BigDecimal, operationType: String, remark: String? = null): Boolean

    /**
     * 解冻余额
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param amount 金额
     * @param operationType 操作类型
     * @param remark 备注
     * @return 是否成功
     */
    fun unfreezeBalance(userId: Long, walletType: Int = 0, amount: BigDecimal, operationType: String, remark: String? = null): Boolean

    /**
     * 检查余额是否足够
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param amount 需要金额
     * @return 是否足够
     */
    fun checkBalanceSufficient(userId: Long, walletType: Int = 0, amount: BigDecimal): Boolean


    /**
     * 根据股票市场获取对应币种
     */
     fun getCoinByStockFlag(stockFlag: String?): String
}
