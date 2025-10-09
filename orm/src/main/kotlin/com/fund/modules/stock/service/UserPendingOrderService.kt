package com.fund.modules.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.stock.StockAddOrderRequest
import com.fund.modules.stock.model.UserPendingOrder

/**
 * <p>
 * 用户挂单表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
interface UserPendingOrderService : IService<UserPendingOrder> {

    /**
     * 添加挂单
     */
    fun addOrder(req: StockAddOrderRequest, userId: Long): R<Any>

    fun delOrder(id: Long, userId: Long): R<Any>

    /**
     * 更新挂单状态
     * @param pendingOrderId 挂单ID
     * @param status 状态 (1=买入成功, 2=买入失败)
     * @param failReason 失败原因
     */
    fun updatePendingOrderStatus(pendingOrderId: Long, status: Int, failReason: String?)
}
