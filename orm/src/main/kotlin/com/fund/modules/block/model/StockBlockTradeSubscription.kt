package com.fund.modules.block.model;

import com.baomidou.mybatisplus.annotation.FieldFill
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
 * 大宗交易申购记录
 * </p>
 *
 * @author 书记
 * @since 2025-10-16
 */
@Schema(description = "大宗交易申购记录")
@TableName("stock_block_trade_subscription")
class StockBlockTradeSubscription : Serializable {

    /**
     * id
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 大宗交易id
     */
    @Schema(description = "大宗交易ID")
    @TableField("block_trade_id")
    var blockTradeId: Long? = null

    /**
     * 订单号
     */
    @Schema(description = "申购订单编号")
    @TableField("order_no")
    var orderNo: String? = null

    /**
     * 用户id
     */
    @Schema(description = "用户ID")
    @TableField("user_id")
    var userId: Long? = null

    /**
     * 上级userid
     */
    @Schema(description = "上级用户ID")
    @TableField("top_user_id")
    var topUserId: Long? = null

    /**
     * 股票名称
     */
    @Schema(description = "股票名称")
    @TableField("name")
    var name: String? = null

    /**
     * 股票id
     */
    @Schema(description = "股票ID")
    @TableField("stock_id")
    var stockId: Long? = null

    /**
     * 购买价格
     */
    @Schema(description = "申购价格")
    @TableField("buy_price")
    var buyPrice: BigDecimal? = null

    /**
     * 申购数量
     */
    @Schema(description = "申购数量")
    @TableField("apply_nums")
    var applyNums: BigDecimal? = null

    /**
     * 折扣
     */
    @Schema(description = "折扣比例")
    @TableField("discount")
    var discount: BigDecimal? = null

    /**
     * 实际支付金额
     */
    @Schema(description = "实际支付金额")
    @TableField("actual_amount")
    var actualAmount: BigDecimal? = null

    /**
     * 状态：1、已申购，2、已取消，3、已确认，4、已转持仓
     */
    @Schema(description = "状态：1已申购 2已取消 3已确认 4已转持仓")
    @TableField("status")
    var status: Int? = null

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 提交时间
     */
    @Schema(description = "提交时间")
    @TableField("submit_time")
    var submitTime: LocalDateTime? = null

    /**
     * 确认时间
     */
    @Schema(description = "确认时间")
    @TableField("confirm_time")
    var confirmTime: LocalDateTime? = null

    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField("remarks")
    var remarks: String? = null

    override fun toString(): String {
        return "StockBlockTradeSubscription{" +
        "id=" + id +
        ", blockTradeId=" + blockTradeId +
        ", orderNo=" + orderNo +
        ", userId=" + userId +
        ", topUserId=" + topUserId +
        ", name=" + name +
        ", stockId=" + stockId +
        ", buyPrice=" + buyPrice +
        ", applyNums=" + applyNums +
        ", discount=" + discount +
        ", actualAmount=" + actualAmount +
        ", status=" + status +
        ", createTime=" + createTime +
        ", submitTime=" + submitTime +
        ", confirmTime=" + confirmTime +
        ", remarks=" + remarks +
        "}"
    }
}

