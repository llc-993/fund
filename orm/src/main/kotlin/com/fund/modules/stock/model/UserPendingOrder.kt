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
 * 用户挂单表
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
@TableName("user_pendingorder")
class UserPendingOrder : Serializable {

    /**
     * 主键，自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Int? = null

    /**
     * 用户ID，关联users表
     */
    @TableField("user_id")
    var userId: Int? = null

    /**
     * 股票代码
     */
    @TableField("stock_id")
    var stockId: String? = null

    /**
     * 买入数量
     */
    @TableField("buy_num")
    var buyNum: Int? = null

    /**
     * 订单类型
     */
    @TableField("buy_type")
    var buyType: Int? = null

    /**
     * 杠杆倍数
     */
    @TableField("lever")
    var lever: Int? = null

    /**
     * 止盈价格
     */
    @TableField("profit_target")
    var profitTarget: BigDecimal? = null

    /**
     * 止损价格
     */
    @TableField("stop_target")
    var stopTarget: BigDecimal? = null

    /**
     * 当前价格
     */
    @TableField("now_price")
    var nowPrice: BigDecimal? = null

    /**
     * 目标价格
     */
    @TableField("target_price")
    var targetPrice: BigDecimal? = null

    /**
     * 订单添加时间
     */
    @TableField("add_time")
    var addTime: LocalDateTime? = null

    /**
     * 状态（0: 已挂单, 1: 买入成功, 2: 买入失败）
     */
    @TableField("status")
    var status: Byte? = null

    /**
     * 买入失败原因
     */
    @TableField("fail_reason")
    var failReason: String? = null

    /**
     * 股票类型
     */
    @TableField("stock_type")
    var stockType: String? = null

    /**
     * 订单编号
     */
    @TableField("order_no")
    var orderNo: String? = null

    override fun toString(): String {
        return "用户挂单表(id=$id, userId=$userId, stockId=$stockId, buyNum=$buyNum, buyType=$buyType, lever=$lever, profitTarget=$profitTarget, stopTarget=$stopTarget, nowPrice=$nowPrice, targetPrice=$targetPrice, addTime=$addTime, status=$status, failReason=$failReason, stockType=$stockType, orderNo=$orderNo)"
    }


}
