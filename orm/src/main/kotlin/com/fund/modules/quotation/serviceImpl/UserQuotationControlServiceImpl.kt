package com.fund.modules.quotation.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.modules.quotation.model.UserQuotationControl
import com.fund.modules.quotation.mapper.UserQuotationControlMapper
import com.fund.modules.quotation.service.UserQuotationControlService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 用户行情调控表 服务实现类
 */
@Service
open class UserQuotationControlServiceImpl : ServiceImpl<UserQuotationControlMapper, UserQuotationControl>(), UserQuotationControlService {

    private val logger = KotlinLogging.logger {}

    override fun setControl(userId: Long, symbol: String, stockType: String, floating: BigDecimal, effectTime: Long, remark: String?): Boolean {
        val existing = getActiveControl(userId, symbol, stockType)
        return if (existing != null) {
            existing.floating = floating
            existing.effectTime = effectTime
            existing.remark = remark
            existing.updatedAt = LocalDateTime.now()
            updateById(existing)
        } else {
            val control = UserQuotationControl().apply {
                this.userId = userId
                this.symbol = symbol
                this.stockType = stockType
                this.floating = floating
                this.effectTime = effectTime
                this.isActive = 1
                this.remark = remark
                this.createdAt = LocalDateTime.now()
                this.updatedAt = LocalDateTime.now()
            }
            save(control)
        }
    }

    override fun getActiveControl(userId: Long, symbol: String, stockType: String): UserQuotationControl? {
        return getOne(
            KtQueryWrapper(UserQuotationControl())
                .eq(UserQuotationControl::userId, userId)
                .eq(UserQuotationControl::symbol, symbol)
                .eq(UserQuotationControl::stockType, stockType)
                .eq(UserQuotationControl::isActive, 1)
                .last(" limit 1 ")
        )
    }

    override fun getBatchControls(userIds: List<Long>, symbol: String, stockType: String): Map<Long, UserQuotationControl> {
        if (userIds.isEmpty()) return emptyMap()
        val list = list(
            KtQueryWrapper(UserQuotationControl())
                .`in`(UserQuotationControl::userId, userIds)
                .eq(UserQuotationControl::symbol, symbol)
                .eq(UserQuotationControl::stockType, stockType)
                .eq(UserQuotationControl::isActive, 1)
        )
        return list.associateBy { it.userId!! }
    }

    override fun getAdjustedPrice(userId: Long, symbol: String, stockType: String, originalPrice: BigDecimal): BigDecimal {
        val control = getActiveControl(userId, symbol, stockType)
        return if (control?.floating != null) {
            originalPrice.add(control.floating)
        } else {
            originalPrice
        }
    }
}
