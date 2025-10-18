package com.fund.modules.wallet.service;

import com.fund.modules.wallet.model.AppUserCashOutOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.cash.CashOutReq

/**
 * <p>
 * 用户提现订单表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-10-18
 */
interface AppUserCashOutOrderService : IService<AppUserCashOutOrder> {

    fun request(userId: Long, req: CashOutReq): R<Any>
}
