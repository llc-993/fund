package com.fund.modules.block.model;

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 股票大宗交易
 * </p>
 *
 * @author 书记
 * @since 2025-10-10
 */
@TableName("stock_block_trade")
class StockBlockTrade : Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Int? = null

    /**
     * 股票名称
     */
    @TableField("name")
    var name: String? = null

    /**
     * 股票id
     */
    @TableField("stock_id")
    var stockId: Int? = null

    /**
     * 最大买入数量
     */
    @TableField("max_amount")
    var maxAmount: BigDecimal? = null

    /**
     * 最小买入数量
     */
    @TableField("min_amount")
    var minAmount: BigDecimal? = null

    /**
     * 锁定状态，1:锁定，2:不锁定
     */
    @TableField("lock_status")
    var lockStatus: Int? = null

    /**
     * 状态
     */
    @TableField("status")
    var status: Int? = null

    /**
     * 折扣
     */
    @TableField("discount")
    var discount: BigDecimal? = null

    /**
     * 开始售卖时间
     */
    @TableField("start_date_time")
    var startDateTime: LocalDateTime? = null

    /**
     * 结束售卖时间
     */
    @TableField("end_date_time")
    var endDateTime: LocalDateTime? = null

    /**
     * 释放时间
     */
    @TableField("release_look_time")
    var releaseLookTime: LocalDateTime? = null

    /**
     * 第一次释放比例
     */
    @TableField("first_release_look_rate")
    var firstReleaseLookRate: BigDecimal? = null

    /**
     * 第一次释放时间
     */
    @TableField("first_release_look_date_time")
    var firstReleaseLookDateTime: LocalDateTime? = null

    override fun toString(): String {
        return "StockBlockTrade{" +
        "id=" + id +
        ", name=" + name +
        ", stockId=" + stockId +
        ", maxAmount=" + maxAmount +
        ", minAmount=" + minAmount +
        ", lockStatus=" + lockStatus +
        ", status=" + status +
        ", discount=" + discount +
        ", startDateTime=" + startDateTime +
        ", endDateTime=" + endDateTime +
        ", releaseLookTime=" + releaseLookTime +
        ", firstReleaseLookRate=" + firstReleaseLookRate +
        ", firstReleaseLookDateTime=" + firstReleaseLookDateTime +
        "}"
    }
}
