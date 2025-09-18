package com.fund.modules.wallet.model

import com.baomidou.mybatisplus.annotation.*
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 用户资金统计表
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
@TableName("app_user_finance_stats")
class AppUserFinanceStats : Serializable {

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
     * 总代用户ID
     */
    @TableField("top_user_id")
    var topUserId: Long? = null

    /**
     * 累计充值
     */
    @TableField("total_recharge")
    var totalRecharge: BigDecimal? = null

    /**
     * 累计提现
     */
    @TableField("total_withdraw")
    var totalWithdraw: BigDecimal? = null

    /**
     * 累计收入
     */
    @TableField("total_income")
    var totalIncome: BigDecimal? = null

    /**
     * 累计佣金
     */
    @TableField("total_commission")
    var totalCommission: BigDecimal? = null

    /**
     * 累计交易量
     */
    @TableField("total_trading_volume")
    var totalTradingVolume: BigDecimal? = null

    /**
     * 最后充值时间
     */
    @TableField("last_recharge_time")
    var lastRechargeTime: LocalDateTime? = null

    /**
     * 最后提现时间
     */
    @TableField("last_withdraw_time")
    var lastWithdrawTime: LocalDateTime? = null

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
        return "AppUserFinanceStats{" +
                "id=" + id +
                ", userId=" + userId +
                ", topUserId=" + topUserId +
                ", totalRecharge=" + totalRecharge +
                ", totalWithdraw=" + totalWithdraw +
                ", totalIncome=" + totalIncome +
                ", totalCommission=" + totalCommission +
                ", totalTradingVolume=" + totalTradingVolume +
                ", lastRechargeTime=" + lastRechargeTime +
                ", lastWithdrawTime=" + lastWithdrawTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}'
    }
}
