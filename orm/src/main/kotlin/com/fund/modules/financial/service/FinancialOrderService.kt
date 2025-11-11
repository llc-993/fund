package com.fund.modules.financial.service

import com.fund.modules.financial.FinancialOrderPurchaseRequest
import com.fund.modules.financial.model.FinancialOrder
import com.baomidou.mybatisplus.extension.service.IService
import java.time.LocalDate

/**
 * <p>
 * 理财订单表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
interface FinancialOrderService : IService<FinancialOrder> {
    
    /**
     * 购买理财产品
     * @param userId 用户ID
     * @param request 申购请求
     * @return 理财订单
     */
    fun purchase(userId: Long, request: FinancialOrderPurchaseRequest): FinancialOrder
    
    /**
     * 结算指定日期的理财收益
     * 将收益计入用户冻结余额，更新订单累计收益、最后收益和结算次数
     * 
     * @param settlementDate 结算日期，默认为昨天
     * @return 结算成功的订单数量
     */
    fun settleInterest(settlementDate: LocalDate = LocalDate.now().minusDays(1)): Int
    

}