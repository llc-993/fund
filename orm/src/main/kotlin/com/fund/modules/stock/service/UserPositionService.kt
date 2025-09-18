package com.fund.modules.stock.service;

import com.fund.modules.stock.model.UserPosition;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.stock.StockBuyRequest
import com.fund.modules.stock.model.Stock
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.RequestBody

/**
 * <p>
 * 用户持仓表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
interface UserPositionService : IService<UserPosition> {

    /**
     * 购买
     */
    fun buy(@RequestBody req: StockBuyRequest, userId: Long, request: HttpServletRequest): R<Any>

    /**
     * 清理持仓缓存
     */
    fun clearPositionCache(userPosition: UserPosition, stock: Stock)

    /**
     * 更新持仓缓存
     */
    fun updatePositionCache(userPosition: UserPosition, stock: Stock)

}
