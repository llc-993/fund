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
     * 如果设置为锁定，转持仓后的股票将被锁定，不能立即卖出
     */
    @TableField("lock_status")
    var lockStatus: Int? = null

    /**
     * 状态，1:开放中，2:已关闭
     */
    @TableField("status")
    var status: Int? = null

    /**
     * 折扣（例如：0.9 表示9折，0.8 表示8折）
     * 最终购买价格 = 股票当前价格 × 折扣
     */
    @TableField("discount")
    var discount: BigDecimal? = null

    /**
     * 开始售卖时间
     * 在此时间之前不允许申购
     */
    @TableField("start_date_time")
    var startDateTime: LocalDateTime? = null

    /**
     * 结束售卖时间
     * 在此时间之后不允许申购
     */
    @TableField("end_date_time")
    var endDateTime: LocalDateTime? = null

    /**
     * 完全释放锁定时间
     * 如果设置了锁定，持仓将在此时间点完全解锁
     */
    @TableField("release_look_time")
    var releaseLookTime: LocalDateTime? = null

    /**
     * 第一次释放比例（例如：0.5 表示释放50%）
     * 在 firstReleaseLookDateTime 时间点，释放此比例的持仓
     */
    @TableField("first_release_look_rate")
    var firstReleaseLookRate: BigDecimal? = null

    /**
     * 第一次释放时间
     * 在此时间点，释放 firstReleaseLookRate 比例的持仓
     * 剩余部分在 releaseLookTime 时间点释放
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
