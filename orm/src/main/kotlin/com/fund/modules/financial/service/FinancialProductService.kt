package com.fund.modules.financial.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.financial.FinancialProductCreateRequest
import com.fund.modules.financial.FinancialProductOfflineRequest
import com.fund.modules.financial.FinancialProductUpdateRequest
import com.fund.modules.financial.model.FinancialProduct

/**
 * <p>
 * 理财产品信息表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
interface FinancialProductService : IService<FinancialProduct> {
    
    /**
     * 分页查询理财产品
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 状态：1-上架 0-下架，null表示查询所有
     * @return 分页数据
     */
    fun pageQuery(pageNum: Int, pageSize: Int, status: Byte?, title: String?, productCode: String?): Page<FinancialProduct>
    
    /**
     * 创建理财产品（默认下架状态）
     * 
     * @param request 创建请求
     * @return 创建的产品
     */
    fun createProduct(request: FinancialProductCreateRequest): FinancialProduct
    
    /**
     * 更新理财产品基本信息
     * 
     * @param request 更新请求
     * @return 更新后的产品
     */
    fun updateProduct(request: FinancialProductUpdateRequest): FinancialProduct
    
    /**
     * 上架理财产品
     * 
     * @param id 产品ID
     * @param remark 上架备注
     * @return 上架后的产品
     */
    fun onlineProduct(id: Long, remark: String?): FinancialProduct
    
    /**
     * 下架理财产品
     * 如果请求中forceRedeemAllOrders为true，则会强制赎回该产品下所有生效中的订单
     * 
     * @param request 下架请求
     * @return 下架后的产品
     */
    fun offlineProduct(request: FinancialProductOfflineRequest): FinancialProduct
}