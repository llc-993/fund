package com.fund.modules.wallet.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.modules.wallet.mapper.AppWalletOperationLogMapper
import com.fund.modules.wallet.model.AppWalletOperationLog
import com.fund.modules.wallet.service.AppWalletOperationLogService
import com.fund.utils.GeneratorIdUtil.generateId
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 钱包操作日志表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
@Service
open class AppWalletOperationLogServiceImpl : ServiceImpl<AppWalletOperationLogMapper, AppWalletOperationLog>(), AppWalletOperationLogService {

    override fun logOperation(
        userId: Long,
        walletType: Int,
        operationType: String,
        amount: BigDecimal,
        beforeBalance: BigDecimal,
        afterBalance: BigDecimal,
        relatedId: Long?,
        relatedType: String?,
        status: Int,
        remark: String?
    ): AppWalletOperationLog {
        val serialNo = operationType + generateId()
        
        val log = AppWalletOperationLog().apply {
            this.serialNo = serialNo
            this.userId = userId
            this.walletType = walletType
            this.operationType = operationType
            this.amount = amount
            this.beforeBalance = beforeBalance
            this.afterBalance = afterBalance
            this.relatedId = relatedId
            this.relatedType = relatedType
            this.status = status
            this.remark = remark
        }
        
        this.save(log)
        return log
    }

    override fun findOperationBySerialNo(serialNo: String): AppWalletOperationLog? {
        return getOne(
            KtQueryWrapper(AppWalletOperationLog())
                .eq(AppWalletOperationLog::serialNo, serialNo)
                .last("limit 1")
        )
    }

    override fun findOperationsByUserId(userId: Long, walletType: Int?, operationType: String?, limit: Int): List<AppWalletOperationLog> {
        val queryWrapper = KtQueryWrapper(AppWalletOperationLog())
            .eq(AppWalletOperationLog::userId, userId)
            .orderByDesc(AppWalletOperationLog::createTime)
            .last("limit $limit")
        
        walletType?.let { queryWrapper.eq(AppWalletOperationLog::walletType, it) }
        operationType?.let { queryWrapper.eq(AppWalletOperationLog::operationType, it) }
        
        return this.list(queryWrapper)
    }

    override fun updateOperationStatus(serialNo: String, status: Int): Boolean {
        val log = findOperationBySerialNo(serialNo) ?: return false
        log.status = status
        return this.updateById(log)
    }
}
