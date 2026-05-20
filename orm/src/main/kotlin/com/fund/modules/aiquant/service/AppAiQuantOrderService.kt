package com.fund.modules.aiquant.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.aiquant.model.AppAiQuantOrder
import com.fund.modules.aiquant.request.AiQuantOrderCreateManageReq
import com.fund.modules.aiquant.request.AiQuantOrderPageManageReq
import com.fund.modules.aiquant.request.AiQuantOrderUpdateManageReq

/**
 * AI 量化展示订单
 */
interface AppAiQuantOrderService : IService<AppAiQuantOrder> {

    /**
     * 管理端创建订单并与周期绑定；须在周期审核通过且尚无关联订单时调用。
     * 事务与分布式锁由实现内结合周期锁统一处理。
     */
    fun createByAdmin(adminId: Long, req: AiQuantOrderCreateManageReq): AppAiQuantOrder

    /** 卖出信息等在完成周期前补齐 */
    fun updateByAdmin(adminId: Long, req: AiQuantOrderUpdateManageReq): AppAiQuantOrder

    fun managePage(query: AiQuantOrderPageManageReq): Page<AppAiQuantOrder>

    /**
     * 根据买卖价计算持仓数量、毛利盈亏、费率与手续费；毛利 >0 时才计手续费。
     */
    fun calcAndApplyProfit(order: AppAiQuantOrder): AppAiQuantOrder
}
