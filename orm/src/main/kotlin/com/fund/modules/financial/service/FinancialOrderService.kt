package com.fund.modules.financial.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.modules.financial.FinancialOrderForceRedeemRequest
import com.fund.modules.financial.FinancialOrderPurchaseRequest
import com.fund.modules.financial.FinancialOrderQueryRequest
import com.fund.modules.financial.FinancialOrderRedeemRequest
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
     * 赎回理财产品
     * 将订单状态更新为已平仓，将累计收益解冻到可用余额
     * 
     * @param userId 用户ID
     * @param request 赎回请求
     * @return 赎回的订单
     */
    fun redeem(userId: Long, request: FinancialOrderRedeemRequest): FinancialOrder
    
    /**
     * 结算指定日期的理财收益
     * 将收益计入用户冻结余额，更新订单累计收益、最后收益和结算次数
     * 
     * @param settlementDate 结算日期，默认为昨天
     * @return 结算成功的订单数量
     */
    fun settleInterest(settlementDate: LocalDate = LocalDate.now().minusDays(1)): Int
    
    /**
     * 分页查询理财订单
     * 
     * @param request 查询条件
     * @return 分页数据
     */
    fun pageQuery(request: FinancialOrderQueryRequest): Page<FinancialOrder>
    
    /**
     * 管理员强制赎回订单
     * 
     * @param request 强制赎回请求
     * @return 赎回的订单
     */
    fun forceRedeem(request: FinancialOrderForceRedeemRequest): FinancialOrder
    
    /**
     * 强制赎回产品下所有生效中的订单
     * 
     * @param productId 产品ID
     * @param remark 备注
     * @return 赎回的订单数量
     */
    fun forceRedeemByProductId(productId: Long, remark: String?): Int
    
    /**
     * 获取用户的所有理财购买记录（不分页）
     *
     * @param userId 用户ID
     * @return 用户的理财订单列表
     */
    fun getUserOrders(userId: Long): List<FinancialOrder>
}