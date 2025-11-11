package com.fund.modules.risingFalling.model;

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
 * 涨跌板块申购记录
 * </p>
 *
 * @author 书记
 * @since 2025-10-17
 */
@Schema(description = "涨跌板块申购记录")
@TableName("rising_falling_sectors_subscription")
class RisingFallingSectorsSubscription : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "申购记录主键ID", example = "1001", nullable = true)
    var id: Long? = null

    /**
     * 涨跌板块ID
     */
    @TableField("rising_falling_sectors_id")
    @Schema(description = "关联的涨跌板块ID", example = "5001", nullable = true)
    var risingFallingSectorsId: Long? = null

    /**
     * 订单号
     */
    @TableField("order_no")
    @Schema(description = "申购订单编号", example = "RF20231012001", nullable = true)
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
    @Schema(description = "股票名称", example = "新能源板块", nullable = true)
    var name: String? = null

    /**
     * 股票ID
     */
    @TableField("stock_id")
    @Schema(description = "股票ID", example = "40001", nullable = true)
    var stockId: Long? = null

    /**
     * 交易对
     */
    @TableField("symbol")
    @Schema(description = "交易对/股票代码", example = "CN600519", nullable = true)
    var symbol: String? = null

    /**
     * 购买价格
     */
    @TableField("buy_price")
    @Schema(description = "申购价格", example = "20.50", nullable = true)
    var buyPrice: BigDecimal? = null

    /**
     * 申购数量
     */
    @TableField("apply_nums")
    @Schema(description = "申购数量", example = "1000", nullable = true)
    var applyNums: BigDecimal? = null

    /**
     * 实际支付金额
     */
    @TableField("actual_amount")
    @Schema(description = "实际支付金额", example = "20500.00", nullable = true)
    var actualAmount: BigDecimal? = null

    /**
     * 状态：1、已申购，2、已取消，3、已确认，4、已转持仓
     */
    @TableField("status")
    @Schema(description = "申购状态（1=已申购，2=已取消，3=已确认，4=已转持仓）", example = "1", nullable = true)
    var status: Int? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "记录创建时间", example = "2025-10-17T10:00:00", nullable = true)
    var createTime: LocalDateTime? = null

    /**
     * 提交时间
     */
    @TableField("submit_time")
    @Schema(description = "申购提交时间", example = "2025-10-17T10:05:00", nullable = true)
    var submitTime: LocalDateTime? = null

    /**
     * 确认时间
     */
    @TableField("confirm_time")
    @Schema(description = "确认时间", example = "2025-10-18T09:30:00", nullable = true)
    var confirmTime: LocalDateTime? = null

    /**
     * 备注
     */
    @TableField("remarks")
    @Schema(description = "备注信息", example = "优先处理", nullable = true)
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
