package com.fund.modules.stock.model

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema
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
@Schema(description = "用户持仓信息")
@TableName("user_position")
class UserPosition : Serializable {

    /**
     * 主键，自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "持仓主键ID", example = "1001", nullable = true)
    var id: Int? = null

    /**
     * 持仓类型
     */
    @TableField("position_type")
    @Schema(description = "持仓类型", example = "1", nullable = true)
    var positionType: Int? = null

    /**
     * 持仓编号
     */
    @TableField("position_sn")
    @Schema(description = "持仓编号", example = "POS20251115001", nullable = true)
    var positionSn: String? = null

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID", example = "20001", nullable = true)
    var userId: Long? = null

    /**
     * 用户昵称
     */
    @TableField("nick_name")
    @Schema(description = "用户昵称", example = "小明", nullable = true)
    var nickName: String? = null

    /**
     * 代理ID
     */
    @TableField("agent_id")
    @Schema(description = "代理ID", example = "3001", nullable = true)
    var agentId: Int? = null

    /**
     * 股票名称
     */
    @TableField("stock_name")
    @Schema(description = "股票名称", example = "苹果公司", nullable = true)
    var stockName: String? = null

    /**
     * 股票代码
     */
    @TableField("stock_code")
    @Schema(description = "股票代码", example = "AAPL", nullable = true)
    var stockCode: String? = null

    /**
     * 股票类型
     */
    @TableField("stock_type")
    @Schema(description = "股票类型，如 US/CN", example = "US", nullable = true)
    var stockType: String? = null

    /**
     * 股票全局ID
     */
    @TableField("stock_gid")
    @Schema(description = "股票全局ID", example = "NASDAQ:AAPL", nullable = true)
    var stockGid: String? = null

    /**
     * 股票拼音
     */
    @TableField("stock_spell")
    @Schema(description = "股票拼音/简写", example = "pingguo", nullable = true)
    var stockSpell: String? = null

    /**
     * 买入订单ID
     */
    @TableField("buy_order_id")
    @Schema(description = "买入订单ID", example = "BUY20251115001", nullable = true)
    var buyOrderId: String? = null

    /**
     * 买入订单时间
     */
    @TableField("buy_order_time")
    @Schema(description = "买入时间", example = "2025-11-15T09:30:00", nullable = true)
    var buyOrderTime: LocalDateTime? = null

    /**
     * 买入订单价格
     */
    @TableField("buy_order_price")
    @Schema(description = "买入价格", example = "150.25", nullable = true)
    var buyOrderPrice: BigDecimal? = null

    /**
     * 卖出订单ID
     */
    @TableField("sell_order_id")
    @Schema(description = "卖出订单ID", example = "SELL20251120001", nullable = true)
    var sellOrderId: String? = null

    /**
     * 卖出订单时间
     */
    @TableField("sell_order_time")
    @Schema(description = "卖出时间", example = "2025-11-20T14:10:00", nullable = true)
    var sellOrderTime: LocalDateTime? = null

    /**
     * 卖出订单价格
     */
    @TableField("sell_order_price")
    @Schema(description = "卖出价格", example = "165.80", nullable = true)
    var sellOrderPrice: BigDecimal? = null

    /**
     * 卖出失败原因
     */
    @TableField("sell_fail_reason")
    @Schema(description = "卖出失败原因", example = "触及价格限制", nullable = true)
    var sellFailReason: String? = null

    /**
     * 目标盈利价格
     */
    @TableField("profit_target_price")
    @Schema(description = "目标盈利价格", example = "180.00", nullable = true)
    var profitTargetPrice: BigDecimal? = null

    /**
     * 止损价格
     */
    @TableField("stop_target_price")
    @Schema(description = "止损价格", example = "140.00", nullable = true)
    var stopTargetPrice: BigDecimal? = null

    /**
     * 订单方向（如买入/卖出）
     */
    @TableField("order_direction")
    @Schema(description = "订单方向（BUY/SELL）", example = "BUY", nullable = true)
    var orderDirection: String? = null

    /**
     * 订单数量
     */
    @TableField("order_num")
    @Schema(description = "订单数量（手数）", example = "10", nullable = true)
    var orderNum: BigDecimal? = null

    /**
     * 杠杆倍数
     */
    @TableField("order_lever")
    @Schema(description = "杠杆倍数", example = "5", nullable = true)
    var orderLever: Int? = null

    /**
     * 订单总金额
     */
    @TableField("order_total_price")
    @Schema(description = "订单总金额（含杠杆）", example = "15000.00", nullable = true)
    var orderTotalPrice: BigDecimal? = null

    /**
     * 订单手续费
     */
    @TableField("order_fee")
    @Schema(description = "订单手续费", example = "12.50", nullable = true)
    var orderFee: BigDecimal? = null

    /**
     * 点差费用
     */
    @TableField("order_spread")
    @Schema(description = "点差费用", example = "3.20", nullable = true)
    var orderSpread: BigDecimal? = null

    /**
     * 持仓过夜费
     */
    @TableField("order_stay_fee")
    @Schema(description = "留仓/过夜费", example = "1.50", nullable = true)
    var orderStayFee: BigDecimal? = null

    /**
     * 持仓天数
     */
    @TableField("order_stay_days")
    @Schema(description = "持仓天数", example = "5", nullable = true)
    var orderStayDays: Int? = null

    /**
     * 盈亏金额
     */
    @TableField("profit_and_lose")
    @Schema(description = "当前盈亏金额", example = "120.75", nullable = true)
    var profitAndLose: BigDecimal? = null

    /**
     * 总盈亏金额
     */
    @TableField("all_profit_and_lose")
    @Schema(description = "累计盈亏金额", example = "350.20", nullable = true)
    var allProfitAndLose: BigDecimal? = null

    /**
     * 是否锁仓（0: 未锁仓, 1: 锁仓）
     */
    @TableField("is_lock")
    @Schema(description = "是否锁仓（0=否，1=是）", example = "0", nullable = true)
    var isLock: Byte? = null

    /**
     * 锁仓信息
     */
    @TableField("lock_msg")
    @Schema(description = "锁仓原因说明", example = "风控锁仓", nullable = true)
    var lockMsg: String? = null

    /**
     * 股票板块
     */
    @TableField("stock_plate")
    @Schema(description = "股票所属板块", example = "科技", nullable = true)
    var stockPlate: String? = null

    /**
     * 点差费用
     */
    @TableField("spread_rate_price")
    @Schema(description = "点差费金额", example = "4.50", nullable = true)
    var spreadRatePrice: BigDecimal? = null

    /**
     * 追加保证金
     */
    @TableField("margin_add")
    @Schema(description = "追加保证金", example = "500.00", nullable = true)
    var marginAdd: BigDecimal? = null

    /**
     * 状态（1: 持仓中, 2: 正在平仓, 3: 已平仓, 4: 平仓失败）
     */
    @TableField("status")
    @Schema(description = "状态（1=持仓中，2=平仓中，3=已平仓，4=失败）", example = "1", nullable = true)
    var status: String? = null

    /**
     * 每手单位数量
     */
    @TableField("lot_unit")
    @Schema(description = "每手单位数量", example = "100", nullable = true)
    var lotUnit: Int? = null

    /**
     * 挂单编号
     */
    @TableField("pendingorder_no")
    @Schema(description = "挂单编号", example = "PEND20251115001", nullable = true)
    var pendingorderNo: String? = null

    @TableField(exist = false)
    var price: BigDecimal? = null

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
