package com.fund.modules.wallet.service

import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.wallet.model.AppWalletOperationLog
import java.math.BigDecimal

/**
 * <p>
 * 钱包操作日志表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
interface AppWalletOperationLogService : IService<AppWalletOperationLog> {

    /**
     * 记录钱包操作日志
     * @param userId 用户ID
     * @param walletType 钱包类型
     * @param operationType 操作类型
     * @param amount 操作金额
     * @param beforeBalance 操作前余额
     * @param afterBalance 操作后余额
     * @param relatedId 关联业务ID
     * @param relatedType 关联业务类型
     * @param status 状态
     * @param remark 备注
     * @return 操作日志
     */
    fun logOperation(
        userId: Long,
        walletType: Int,
        operationType: String,
        amount: BigDecimal,
        beforeBalance: BigDecimal,
        afterBalance: BigDecimal,
        relatedId: Long? = null,
        relatedType: String? = null,
        status: Int = 1,
        remark: String? = null
    ): AppWalletOperationLog

    /**
     * 根据流水号查询操作日志
     * @param serialNo 流水号
     * @return 操作日志
     */
    fun findOperationBySerialNo(serialNo: String): AppWalletOperationLog?

    /**
     * 根据用户ID查询操作日志列表
     * @param userId 用户ID
     * @param walletType 钱包类型（可选）
     * @param operationType 操作类型（可选）
     * @param limit 限制条数
     * @return 操作日志列表
     */
    fun findOperationsByUserId(userId: Long, walletType: Int? = null, operationType: String? = null, limit: Int = 100): List<AppWalletOperationLog>

    /**
     * 更新操作状态
     * @param serialNo 流水号
     * @param status 新状态
     * @return 是否成功
     */
    fun updateOperationStatus(serialNo: String, status: Int): Boolean
}
