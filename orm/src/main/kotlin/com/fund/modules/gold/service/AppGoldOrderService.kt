package com.fund.modules.gold.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.gold.model.AppGoldOrder
import com.fund.modules.gold.request.GoldBuyReq
import com.fund.modules.gold.request.GoldOrderPageReq
import com.fund.modules.gold.request.GoldSellReq

/** 积存金订单 */
interface AppGoldOrderService : IService<AppGoldOrder> {
    fun userBuy(userId: Long, req: GoldBuyReq): AppGoldOrder
    fun userSell(userId: Long, req: GoldSellReq): AppGoldOrder
    fun pageMyOrders(userId: Long, req: GoldOrderPageReq): Page<AppGoldOrder>
    fun managePage(req: GoldOrderPageReq): Page<AppGoldOrder>
}
