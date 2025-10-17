package com.fund.modules.block.service;

import com.fund.modules.block.model.StockBlockTradeSubscription;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.block.BlockTradeApplyRequest
import com.fund.modules.block.BlockTradeUpdateRequest

/**
 * <p>
 * 大宗交易申购记录 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-10-16
 */
interface StockBlockTradeSubscriptionService : IService<StockBlockTradeSubscription> {

    /**
     * 申购大宗交易
     */
    fun apply(req: BlockTradeApplyRequest, userId: Long): R<Any>

    /**
     * 申购历史
     */
    fun history(userId: Long): R<Any>

    /**
     * 修改申购
     */
    fun update(req: BlockTradeUpdateRequest, userId: Long): R<Any>
}

