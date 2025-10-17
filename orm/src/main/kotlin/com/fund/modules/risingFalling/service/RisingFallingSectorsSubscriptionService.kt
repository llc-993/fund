package com.fund.modules.risingFalling.service;

import com.fund.modules.risingFalling.model.RisingFallingSectorsSubscription;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.risingFalling.RisingFallingSectorsApplyRequest
import com.fund.modules.risingFalling.RisingFallingSectorsUpdateRequest

/**
 * <p>
 * 涨跌板块申购记录 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-10-17
 */
interface RisingFallingSectorsSubscriptionService : IService<RisingFallingSectorsSubscription> {

    /**
     * 申购涨跌板块
     */
    fun apply(req: RisingFallingSectorsApplyRequest, userId: Long): R<Any>

    /**
     * 申购历史
     */
    fun history(userId: Long): R<Any>

    /**
     * 修改申购
     */
    fun update(req: RisingFallingSectorsUpdateRequest, userId: Long): R<Any>
}
