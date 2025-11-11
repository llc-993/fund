package com.fund.modules.ipo.model;

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
 * 新股申购
 * </p>
 *
 * @author 书记
 * @since 2025-10-07
 */
@Schema(description = "新股申购订单信息")
@TableName("stock_subscription")
class StockSubscription : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "申购记录主键ID", example = "1001", nullable = true)
    var id: Long? = null

    @TableField("ipo_id")
    @Schema(description = "关联的新股发行ID", example = "5001", nullable = true)
    var ipoId: Long? = null

    /**
     * 订单号
     */
    @TableField("order_no")
    @Schema(description = "新股申购订单编号", example = "IPO20231012001", nullable = true)
    var orderNo: String? = null

    /**
     * 用户id
     */
    @TableField("user_id")
    @Schema(description = "申购用户ID", example = "20001", nullable = true)
    var userId: Long? = null

    /**
     * 上级userid
     */
    @TableField("top_user_id")
    @Schema(description = "上级代理用户ID", example = "30001", nullable = true)
    var topUserId: Long? = null

    /**
     * 股票名称
     */
    @TableField("name")
    @Schema(description = "申购股票名称", example = "申购科技", nullable = true)
    var name: String? = null

    /**
     * 购买价格
     */
    @TableField("buy_price")
    @Schema(description = "申购价格", example = "18.50", nullable = true)
    var buyPrice: BigDecimal? = null

    /**
     * 股票代码
     */
    @TableField("symbol")
    @Schema(description = "股票代码/交易代码", example = "IPO1234", nullable = true)
    var symbol: String? = null

    /**
     * 申购数量
     */
    @TableField("apply_nums")
    @Schema(description = "申购数量（股）", example = "1000", nullable = true)
    var applyNums: BigDecimal? = null

    /**
     * 中签数量
     */
    @TableField("allotment_quantity")
    @Schema(description = "中签数量（股）", example = "500", nullable = true)
    var allotmentQuantity: BigDecimal? = null

    /**
     * 状态：1、已认购，2、未中签，3、已中签，4、已缴纳 5.已转持仓
     */
    @TableField("status")
    @Schema(description = "申购状态（1=已认购，2=未中签，3=已中签，4=已缴纳，5=已转持仓）", example = "1", nullable = true)
    var status: Int? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "记录创建时间", example = "2025-10-07T10:00:00", nullable = true)
    var createTime: LocalDateTime? = null

    /**
     * 提交时间
     */
    @TableField("submit_time")
    @Schema(description = "申购提交时间", example = "2025-10-07T10:05:00", nullable = true)
    var submitTime: LocalDateTime? = null

    /**
     * 申购时间
     */
    @TableField("allotment_time")
    @Schema(description = "中签或结果公布时间", example = "2025-10-08T09:30:00", nullable = true)
    var allotmentTime: LocalDateTime? = null

    /**
     * 双融确定时间
     */
    @TableField("fix_time")
    @Schema(description = "配售或双融确定时间", example = "2025-10-09T14:00:00", nullable = true)
    var fixTime: LocalDateTime? = null

    /**
     * 备注
     */
    @TableField("remarks")
    @Schema(description = "附加备注信息", example = "优先分配", nullable = true)
    var remarks: String? = null

    /**
     * 1 新股 2配售
     */
    @TableField("type")
    @Schema(description = "申购类型（1=新股，2=配售）", example = "1", nullable = true)
    var type: Int? = null

    /**
     * 新股类型
     */
    @TableField("stock_type")
    @Schema(description = "股票市场类型，如 US、CN 等", example = "CN", nullable = true)
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
