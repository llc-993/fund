package com.fund.modules.ipo.service;

import com.fund.modules.ipo.model.StockSubscription;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.ipo.IpoApplyRequest
import com.fund.modules.ipo.IpoUpdateRequest
import org.springframework.web.bind.annotation.RequestBody

/**
 * <p>
 * 新股申购 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-10-07
 */
interface StockSubscriptionService : IService<StockSubscription> {

    /**
     * ipo申购
     */
    fun apply(req: IpoApplyRequest, userId: Long): R<Any>

    /**
     * 购买历史
     */
    fun history(userId: Long):R<Any>

    fun update(@RequestBody req: IpoUpdateRequest, userId: Long): R<Any>
}
