package com.fund.modules.ipo.model;

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
 * 新股申购
 * </p>
 *
 * @author 书记
 * @since 2025-10-07
 */
@TableName("stock_subscription")
class StockSubscription : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    @TableField("ipo_id")
    var ipoId: Long? = null

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
     * 购买价格
     */
    @TableField("buy_price")
    var buyPrice: BigDecimal? = null

    /**
     * 股票代码
     */
    @TableField("symbol")
    var symbol: String? = null

    /**
     * 申购数量
     */
    @TableField("apply_nums")
    var applyNums: BigDecimal? = null

    /**
     * 中签数量
     */
    @TableField("allotment_quantity")
    var allotmentQuantity: BigDecimal? = null

    /**
     * 状态：1、已认购，2、未中签，3、已中签，4、已缴纳 5.已转持仓
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
     * 申购时间
     */
    @TableField("allotment_time")
    var allotmentTime: LocalDateTime? = null

    /**
     * 双融确定时间
     */
    @TableField("fix_time")
    var fixTime: LocalDateTime? = null

    /**
     * 备注
     */
    @TableField("remarks")
    var remarks: String? = null

    /**
     * 1 新股 2配售
     */
    @TableField("type")
    var type: Int? = null

    /**
     * 新股类型
     */
    @TableField("stock_type")
    var stockType: String? = null

    override fun toString(): String {
        return "StockSubscription{" +
        "id=" + id +
        ", orderNo=" + orderNo +
        ", userId=" + userId +
        ", topUserId=" + topUserId +
        ", name=" + name +
        ", buyPrice=" + buyPrice +
        ", symbol=" + symbol +
        ", applyNums=" + applyNums +
        ", allotmentQuantity=" + allotmentQuantity +
        ", status=" + status +
        ", createTime=" + createTime +
        ", submitTime=" + submitTime +
        ", allotmentTime=" + allotmentTime +
        ", fixTime=" + fixTime +
        ", remarks=" + remarks +
        ", type=" + type +
        ", stockType=" + stockType +
        "}"
    }
}
