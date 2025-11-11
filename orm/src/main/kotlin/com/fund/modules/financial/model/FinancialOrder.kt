package com.fund.modules.financial.model;

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
 * 理财订单表
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
@TableName("financial_order")
class FinancialOrder : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 用户ID
     */
    @TableField("user_id")
    var userId: Long? = null

    /**
     * 订单编号
     */
    @TableField("order_no")
    var orderNo: String? = null

    /**
     * 理财产品ID
     */
    @TableField("product_id")
    var productId: Long? = null

    /**
     * 产品编码（冗余）
     */
    @TableField("product_code")
    var productCode: String? = null

    /**
     * 产品名称（冗余）
     */
    @TableField("product_name")
    var productName: String? = null

    /**
     * 投资金额
     */
    @TableField("invest_amount")
    var investAmount: BigDecimal? = null

    /**
     * 投资期限（天数），活期可为空
     */
    @TableField("invest_period")
    var investPeriod: Int? = null

    /**
     * 结算币种
     */
    @TableField("coin")
    var coin: String? = null

    /**
     * 利率类型：1-活期 2-固定
     */
    @TableField("rate_type")
    var rateType: Byte? = null

    /**
     * 最小利率
     */
    @TableField("min_rate")
    var minRate: BigDecimal? = null

    /**
     * 最大利率
     */
    @TableField("max_rate")
    var maxRate: BigDecimal? = null

    /**
     * 违约/基准利率
     */
    @TableField("default_rate")
    var defaultRate: BigDecimal? = null

    /**
     * 实际生效利率
     */
    @TableField("actual_rate")
    var actualRate: BigDecimal? = null

    /**
     * 订单状态：1-生效中 2-已平仓 3-已过期
     */
    @TableField("order_status")
    var orderStatus: Byte? = null

    /**
     * 开始计息时间
     */
    @TableField("start_time")
    var startTime: LocalDateTime? = null

    /**
     * 下次结算时间
     */
    @TableField("next_settle_time")
    var nextSettleTime: LocalDateTime? = null

    /**
     * 到期时间（固定期限适用）
     */
    @TableField("expire_time")
    var expireTime: LocalDateTime? = null

    /**
     * 实际平仓/结束时间
     */
    @TableField("close_time")
    var closeTime: LocalDateTime? = null

    /**
     * 累计收益
     */
    @TableField("accumulated_profit")
    var accumulatedProfit: BigDecimal? = null

    /**
     * 上次结算收益
     */
    @TableField("last_profit")
    var lastProfit: BigDecimal? = null

    /**
     * 已结算次数
     */
    @TableField("settled_count")
    var settledCount: Int? = null

    /**
     * 结算周期：daily/weekly/monthly
     */
    @TableField("settle_cycle")
    var settleCycle: String? = null

    /**
     * 扣款钱包类型：0-主钱包 1-交易钱包
     */
    @TableField("wallet_type")
    var walletType: Byte? = null

    /**
     * 订单备注
     */
    @TableField("remark")
    var remark: String? = null

    /**
     * 申购时间
     */
    @TableField("apply_time")
    var applyTime: LocalDateTime? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
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
