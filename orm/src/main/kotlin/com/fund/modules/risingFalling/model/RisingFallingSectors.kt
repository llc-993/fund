package com.fund.modules.risingFalling.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 涨跌板块
 * </p>
 *
 * @author 书记
 * @since 2025-10-17
 */
@TableName("rising_falling_sectors")
class RisingFallingSectors : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 交易对
     */
    @TableField("symbol")
    var symbol: String? = null

    /**
     * stock 的id
     */
    @TableField("stock_id")
    var stockId: Long? = null

    /**
     * 股票锁定状态
     */
    @TableField("stock_lock_status")
    var stockLockStatus: Int? = null

    /**
     * 显示状态(0:显示，1:隐藏)
     */
    @TableField("display_status")
    var displayStatus: Int? = null

    /**
     * 开始时间
     */
    @TableField("open_time")
    var openTime: LocalDateTime? = null

    /**
     * 结束时间
     */
    @TableField("end_time")
    var endTime: LocalDateTime? = null

    /**
     * 开始售卖时间
     */
    @TableField("start_sell_time")
    var startSellTime: LocalDateTime? = null

    /**
     * 结束售卖的时间
     */
    @TableField("end_sell_time")
    var endSellTime: LocalDateTime? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @TableField(value = "pass_word")
    var passWord: String? = null

    override fun toString(): String {
        return "RisingFallingSectors(id=$id, symbol=$symbol, stockId=$stockId, stockLockStatus=$stockLockStatus, displayStatus=$displayStatus, openTime=$openTime, endTime=$endTime, startSellTime=$startSellTime, endSellTime=$endSellTime, createTime=$createTime, passWord=$passWord)"
    }


}
