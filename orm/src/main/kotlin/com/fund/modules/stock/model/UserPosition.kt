package com.fund.modules.stock.model;

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 用户持仓表
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
@TableName("user_position")
class UserPosition : Serializable {

    /**
     * 主键，自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Int? = null

    /**
     * 持仓类型
     */
    @TableField("position_type")
    var positionType: Int? = null

    /**
     * 持仓编号
     */
    @TableField("position_sn")
    var positionSn: String? = null

    /**
     * 用户ID
     */
    @TableField("user_id")
    var userId: Int? = null

    /**
     * 用户昵称
     */
    @TableField("nick_name")
    var nickName: String? = null

    /**
     * 代理ID
     */
    @TableField("agent_id")
    var agentId: Int? = null

    /**
     * 股票名称
     */
    @TableField("stock_name")
    var stockName: String? = null

    /**
     * 股票代码
     */
    @TableField("stock_code")
    var stockCode: String? = null

    /**
     * 股票类型
     */
    @TableField("stock_type")
    var stockType: String? = null

    /**
     * 股票全局ID
     */
    @TableField("stock_gid")
    var stockGid: String? = null

    /**
     * 股票拼音
     */
    @TableField("stock_spell")
    var stockSpell: String? = null

    /**
     * 买入订单ID
     */
    @TableField("buy_order_id")
    var buyOrderId: String? = null

    /**
     * 买入订单时间
     */
    @TableField("buy_order_time")
    var buyOrderTime: LocalDateTime? = null

    /**
     * 买入订单价格
     */
    @TableField("buy_order_price")
    var buyOrderPrice: BigDecimal? = null

    /**
     * 卖出订单ID
     */
    @TableField("sell_order_id")
    var sellOrderId: String? = null

    /**
     * 卖出订单时间
     */
    @TableField("sell_order_time")
    var sellOrderTime: LocalDateTime? = null

    /**
     * 卖出订单价格
     */
    @TableField("sell_order_price")
    var sellOrderPrice: BigDecimal? = null

    /**
     * 卖出失败原因
     */
    @TableField("sell_fail_reason")
    var sellFailReason: String? = null

    /**
     * 目标盈利价格
     */
    @TableField("profit_target_price")
    var profitTargetPrice: BigDecimal? = null

    /**
     * 止损价格
     */
    @TableField("stop_target_price")
    var stopTargetPrice: BigDecimal? = null

    /**
     * 订单方向（如买入/卖出）
     */
    @TableField("order_direction")
    var orderDirection: String? = null

    /**
     * 订单数量
     */
    @TableField("order_num")
    var orderNum: BigDecimal? = null

    /**
     * 杠杆倍数
     */
    @TableField("order_lever")
    var orderLever: Int? = null

    /**
     * 订单总金额
     */
    @TableField("order_total_price")
    var orderTotalPrice: BigDecimal? = null

    /**
     * 订单手续费
     */
    @TableField("order_fee")
    var orderFee: BigDecimal? = null

    /**
     * 点差费用
     */
    @TableField("order_spread")
    var orderSpread: BigDecimal? = null

    /**
     * 持仓过夜费
     */
    @TableField("order_stay_fee")
    var orderStayFee: BigDecimal? = null

    /**
     * 持仓天数
     */
    @TableField("order_stay_days")
    var orderStayDays: Int? = null

    /**
     * 盈亏金额
     */
    @TableField("profit_and_lose")
    var profitAndLose: BigDecimal? = null

    /**
     * 总盈亏金额
     */
    @TableField("all_profit_and_lose")
    var allProfitAndLose: BigDecimal? = null

    /**
     * 是否锁仓（0: 未锁仓, 1: 锁仓）
     */
    @TableField("is_lock")
    var isLock: Byte? = null

    /**
     * 锁仓信息
     */
    @TableField("lock_msg")
    var lockMsg: String? = null

    /**
     * 股票板块
     */
    @TableField("stock_plate")
    var stockPlate: String? = null

    /**
     * 点差费用
     */
    @TableField("spread_rate_price")
    var spreadRatePrice: BigDecimal? = null

    /**
     * 追加保证金
     */
    @TableField("margin_add")
    var marginAdd: BigDecimal? = null

    /**
     * 状态（1: 持仓中, 2: 正在平仓, 3: 已平仓, 4: 平仓失败）
     */
    @TableField("status")
    var status: String? = null

    /**
     * 每手单位数量
     */
    @TableField("lot_unit")
    var lotUnit: Int? = null

    /**
     * 挂单编号
     */
    @TableField("pendingorder_no")
    var pendingorderNo: String? = null

    override fun toString(): String {
        return "UserPosition{" +
        "id=" + id +
        ", positionType=" + positionType +
        ", positionSn=" + positionSn +
        ", userId=" + userId +
        ", nickName=" + nickName +
        ", agentId=" + agentId +
        ", stockName=" + stockName +
        ", stockCode=" + stockCode +
        ", stockType=" + stockType +
        ", stockGid=" + stockGid +
        ", stockSpell=" + stockSpell +
        ", buyOrderId=" + buyOrderId +
        ", buyOrderTime=" + buyOrderTime +
        ", buyOrderPrice=" + buyOrderPrice +
        ", sellOrderId=" + sellOrderId +
        ", sellOrderTime=" + sellOrderTime +
        ", sellOrderPrice=" + sellOrderPrice +
        ", sellFailReason=" + sellFailReason +
        ", profitTargetPrice=" + profitTargetPrice +
        ", stopTargetPrice=" + stopTargetPrice +
        ", orderDirection=" + orderDirection +
        ", orderNum=" + orderNum +
        ", orderLever=" + orderLever +
        ", orderTotalPrice=" + orderTotalPrice +
        ", orderFee=" + orderFee +
        ", orderSpread=" + orderSpread +
        ", orderStayFee=" + orderStayFee +
        ", orderStayDays=" + orderStayDays +
        ", profitAndLose=" + profitAndLose +
        ", allProfitAndLose=" + allProfitAndLose +
        ", isLock=" + isLock +
        ", lockMsg=" + lockMsg +
        ", stockPlate=" + stockPlate +
        ", spreadRatePrice=" + spreadRatePrice +
        ", marginAdd=" + marginAdd +
        ", status=" + status +
        ", lotUnit=" + lotUnit +
        ", pendingorderNo=" + pendingorderNo +
        "}"
    }
}
