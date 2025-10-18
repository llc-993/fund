package com.fund.modules.wallet.service;

import com.fund.modules.wallet.model.AppUserCashInOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.cash.CashInReq

/**
 * <p>
 * 用户充值订单表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-10-18
 */
interface AppUserCashInOrderService : IService<AppUserCashInOrder> {

    /**
     * 申请
     */
    fun request(userId: Long, req: CashInReq): R<Any>

}
