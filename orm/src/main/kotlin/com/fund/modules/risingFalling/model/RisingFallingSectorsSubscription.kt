package com.fund.modules.risingFalling.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 涨跌板块申购记录
 * </p>
 *
 * @author 书记
 * @since 2025-10-17
 */
@TableName("rising_falling_sectors_subscription")
class RisingFallingSectorsSubscription : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 涨跌板块ID
     */
    @TableField("rising_falling_sectors_id")
    var risingFallingSectorsId: Long? = null

    /**
     * 订单号
     */
    @TableField("order_no")
    var orderNo: String? = null

    /**
     * 用户id
     */
    @TableField("user_id")
    var userId: Long? = null

    /**
     * 上级userid
     */
    @TableField("top_user_id")
    var topUserId: Long? = null

    /**
     * 股票名称
     */
    @TableField("name")
    var name: String? = null

    /**
     * 股票ID
     */
    @TableField("stock_id")
    var stockId: Long? = null

    /**
     * 交易对
     */
    @TableField("symbol")
    var symbol: String? = null

    /**
     * 购买价格
     */
    @TableField("buy_price")
    var buyPrice: BigDecimal? = null

    /**
     * 申购数量
     */
    @TableField("apply_nums")
    var applyNums: BigDecimal? = null

    /**
     * 实际支付金额
     */
    @TableField("actual_amount")
    var actualAmount: BigDecimal? = null

    /**
     * 状态：1、已申购，2、已取消，3、已确认，4、已转持仓
     */
    @TableField("status")
    var status: Int? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 提交时间
     */
    @TableField("submit_time")
    var submitTime: LocalDateTime? = null

    /**
     * 确认时间
     */
    @TableField("confirm_time")
    var confirmTime: LocalDateTime? = null

    /**
     * 备注
     */
    @TableField("remarks")
    var remarks: String? = null

    override fun toString(): String {
        return "RisingFallingSectorsSubscription{" +
        "id=" + id +
        ", risingFallingSectorsId=" + risingFallingSectorsId +
        ", orderNo=" + orderNo +
        ", userId=" + userId +
        ", topUserId=" + topUserId +
        ", name=" + name +
        ", stockId=" + stockId +
        ", symbol=" + symbol +
        ", buyPrice=" + buyPrice +
        ", applyNums=" + applyNums +
        ", actualAmount=" + actualAmount +
        ", status=" + status +
        ", createTime=" + createTime +
        ", submitTime=" + submitTime +
        ", confirmTime=" + confirmTime +
        ", remarks=" + remarks +
        "}"
    }
}
