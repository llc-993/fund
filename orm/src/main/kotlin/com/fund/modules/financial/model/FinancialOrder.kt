package com.fund.modules.financial.model;

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
 * 理财订单表
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
@Schema(description = "理财订单信息")
@TableName("financial_order")
class FinancialOrder : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "订单ID", example = "5001", nullable = true)
    var id: Long? = null

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID", example = "10001", nullable = true)
    var userId: Long? = null

    /**
     * 订单编号
     */
    @TableField("order_no")
    @Schema(description = "订单编号", example = "FO20251110001", nullable = true)
    var orderNo: String? = null

    /**
     * 理财产品ID
     */
    @TableField("product_id")
    @Schema(description = "理财产品ID", example = "2001", nullable = true)
    var productId: Long? = null

    /**
     * 产品编码（冗余）
     */
    @TableField("product_code")
    @Schema(description = "产品编码（冗余）", example = "FP202511", nullable = true)
    var productCode: String? = null

    /**
     * 产品名称（冗余）
     */
    @TableField("product_name")
    @Schema(description = "产品名称（冗余）", example = "30天稳健理财", nullable = true)
    var productName: String? = null

    /**
     * 投资金额
     */
    @TableField("invest_amount")
    @Schema(description = "投资金额", example = "1000.00", nullable = true)
    var investAmount: BigDecimal? = null

    /**
     * 投资期限（天数），活期可为空
     */
    @TableField("invest_period")
    @Schema(description = "投资期限（天），活期为空", example = "30", nullable = true)
    var investPeriod: Int? = null

    /**
     * 结算币种
     */
    @TableField("coin")
    @Schema(description = "结算币种", example = "USDT", nullable = true)
    var coin: String? = null

    /**
     * 利率类型：1-活期 2-固定
     */
    @TableField("rate_type")
    @Schema(description = "利率类型（1=活期，2=固定）", example = "2", nullable = true)
    var rateType: Byte? = null

    /**
     * 最小利率
     */
    @TableField("min_rate")
    @Schema(description = "最小收益率", example = "0.10", nullable = true)
    var minRate: BigDecimal? = null

    /**
     * 最大利率
     */
    @TableField("max_rate")
    @Schema(description = "最大收益率", example = "0.15", nullable = true)
    var maxRate: BigDecimal? = null

    /**
     * 违约/基准利率
     */
    @TableField("default_rate")
    @Schema(description = "违约/基准利率", example = "0.12", nullable = true)
    var defaultRate: BigDecimal? = null

    /**
     * 实际生效利率
     */
    @TableField("actual_rate")
    @Schema(description = "实际生效利率", example = "0.118", nullable = true)
    var actualRate: BigDecimal? = null

    /**
     * 订单状态：1-生效中 2-已平仓 3-已过期
     */
    @TableField("order_status")
    @Schema(description = "订单状态（1=生效中，2=已平仓，3=已过期）", example = "1", nullable = true)
    var orderStatus: Byte? = null

    /**
     * 开始计息时间
     */
    @TableField("start_time")
    @Schema(description = "开始计息时间", example = "2025-11-10T08:00:00", nullable = true)
    var startTime: LocalDateTime? = null

    /**
     * 下次结算时间
     */
    @TableField("next_settle_time")
    @Schema(description = "下次结算时间", example = "2025-11-11T08:00:00", nullable = true)
    var nextSettleTime: LocalDateTime? = null

    /**
     * 到期时间（固定期限适用）
     */
    @TableField("expire_time")
    @Schema(description = "产品到期时间", example = "2025-12-10T08:00:00", nullable = true)
    var expireTime: LocalDateTime? = null

    /**
     * 实际平仓/结束时间
     */
    @TableField("close_time")
    @Schema(description = "实际平仓/结束时间", example = "2025-12-11T09:00:00", nullable = true)
    var closeTime: LocalDateTime? = null

    /**
     * 累计收益
     */
    @TableField("accumulated_profit")
    @Schema(description = "累计收益", example = "35.68", nullable = true)
    var accumulatedProfit: BigDecimal? = null

    /**
     * 上次结算收益
     */
    @TableField("last_profit")
    @Schema(description = "上次结算收益", example = "1.23", nullable = true)
    var lastProfit: BigDecimal? = null

    /**
     * 已结算次数
     */
    @TableField("settled_count")
    @Schema(description = "已结算次数", example = "10", nullable = true)
    var settledCount: Int? = null

    /**
     * 结算周期：daily/weekly/monthly
     */
    @TableField("settle_cycle")
    @Schema(description = "结算周期（daily/weekly/monthly）", example = "daily", nullable = true)
    var settleCycle: String? = null

    /**
     * 扣款钱包类型：0-主钱包 1-交易钱包
     */
    @TableField("wallet_type")
    @Schema(description = "扣款钱包类型（0=主钱包，1=交易钱包）", example = "0", nullable = true)
    var walletType: Byte? = null

    /**
     * 订单备注
     */
    @TableField("remark")
    @Schema(description = "订单备注", example = "提前赎回需收取手续费", nullable = true)
    var remark: String? = null

    /**
     * 申购时间
     */
    @TableField("apply_time")
    @Schema(description = "申购时间", example = "2025-11-10T07:55:00", nullable = true)
    var applyTime: LocalDateTime? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2025-11-10T07:55:00", nullable = true)
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2025-11-12T10:00:00", nullable = true)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "FinancialOrder{" +
        "id=" + id +
        ", userId=" + userId +
        ", orderNo=" + orderNo +
        ", productId=" + productId +
        ", productCode=" + productCode +
        ", productName=" + productName +
        ", investAmount=" + investAmount +
        ", investPeriod=" + investPeriod +
        ", coin=" + coin +
        ", rateType=" + rateType +
        ", minRate=" + minRate +
        ", maxRate=" + maxRate +
        ", defaultRate=" + defaultRate +
        ", actualRate=" + actualRate +
        ", orderStatus=" + orderStatus +
        ", startTime=" + startTime +
        ", nextSettleTime=" + nextSettleTime +
        ", expireTime=" + expireTime +
        ", closeTime=" + closeTime +
        ", accumulatedProfit=" + accumulatedProfit +
        ", lastProfit=" + lastProfit +
        ", settledCount=" + settledCount +
        ", settleCycle=" + settleCycle +
        ", walletType=" + walletType +
        ", remark=" + remark +
        ", applyTime=" + applyTime +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}"
    }
}
