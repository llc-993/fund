package com.fund.modules.stock.service;

import com.fund.modules.stock.model.UserPosition;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.stock.StockAddOrderRequest
import com.fund.modules.stock.StockBuyRequest
import com.fund.modules.stock.UpdateProfitTargetRequest
import com.fund.modules.stock.model.Stock
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.RequestBody
import java.math.BigDecimal

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
    fun buy(req: StockBuyRequest, userId: Long, request: HttpServletRequest): R<Any>

    /**
     * 从挂单买入
     */
    fun buyFromPendingOrder(pendingOrderId: Long, userId: Long, currentPrice: BigDecimal): R<Any>

    /**
     * 清理持仓缓存
     */
    fun clearPositionCache(userPosition: UserPosition, stock: Stock)

    /**
     * 更新持仓缓存
     */
    fun updatePositionCache(userPosition: UserPosition, stock: Stock)

    /**
     * 根据订单号查找持仓
     */
    fun findPositionBySn(positionBySn: String): UserPosition

    /**
     * 卖
     */
    fun sell(positionSn: String, userId: Long, doType: Int, actionType: String): R<Any>

    fun validateBuyQuantity(buyQuantity: BigDecimal): Boolean

    fun updateProfitTarget( req: UpdateProfitTargetRequest, userId: Long): R<Any>

    /**
     * 计算平仓盈亏
     * @param position 持仓对象
     * @param closePrice 平仓价格
     * @return 盈亏金额
     */
    fun calculateCloseProfitLoss(position: UserPosition, closePrice: BigDecimal): BigDecimal

    /**
     * 计算平仓总费用
     * @param position 持仓对象
     * @param sellAmount 卖出总金额
     * @return 总费用（包含买入手续费、卖出手续费、印花税、留仓费、点差费）
     */
    fun calculateCloseFees(position: UserPosition, sellAmount: BigDecimal): BigDecimal

    /**
     * 执行平仓钱包结算
     * @param position 持仓对象
     * @param stock 股票信息
     * @param totalProfit 总盈亏（扣除费用后）
     * @param freezeAmount 保证金
     * @param remark 备注
     */
    fun settleCloseWallet(position: UserPosition, stock: Stock, totalProfit: BigDecimal, freezeAmount: BigDecimal, remark: String)

    /**
     * 清理平仓缓存（包括止盈止损缓存和持仓缓存）
     * @param position 持仓对象
     * @param stock 股票信息
     */
    fun clearCloseCache(position: UserPosition, stock: Stock)

    fun isTradingTime(stockFlag: String): Boolean
}
