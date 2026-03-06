package com.fund.modules.quotation.service

import com.fund.modules.quotation.model.UserQuotationControl
import com.baomidou.mybatisplus.extension.service.IService
import java.math.BigDecimal

/**
 * 用户行情调控表 服务类
 */
interface UserQuotationControlService : IService<UserQuotationControl> {

    /**
     * 设置用户调控
     */
    fun setControl(userId: Long, symbol: String, stockType: String, floating: BigDecimal, effectTime: Long, remark: String?): Boolean

    /**
     * 获取启用的调控配置
     */
    fun getActiveControl(userId: Long, symbol: String, stockType: String): UserQuotationControl?

    /**
     * 批量获取用户调控(用于持仓处理)
     */
    fun getBatchControls(userIds: List<Long>, symbol: String, stockType: String): Map<Long, UserQuotationControl>

    /**
     * 获取调整后的价格
     */
    fun getAdjustedPrice(userId: Long, symbol: String, stockType: String, originalPrice: BigDecimal): BigDecimal
}
