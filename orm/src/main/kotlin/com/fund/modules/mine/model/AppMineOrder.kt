package com.fund.modules.mine.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 锁仓挖矿项目订单
 * </p>
 *
 * @author 书记
 * @since 2025-12-29
 */
@TableName("app_mine_order")
class AppMineOrder : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 会员id
     */
    @TableField("user_id")
    var userId: Long? = null

    /**
     * uid
     */
    @TableField("uid")
    var uid: Long? = null

    /**
     * 会员名称
     */
    @TableField("user_name")
    var userName: String? = null

    /**
     * 正常 0 假人 1 机器人 2
     */
    @TableField("user_group")
    var userGroup: Int? = null

    /**
     * 一级代理id
     */
    @TableField("t1_id")
    var t1Id: Long? = null

    /**
     * 二级代理ID
     */
    @TableField("t2_id")
    var t2Id: Long? = null

    /**
     * 三级代理id
     */
    @TableField("t3_id")
    var t3Id: Long? = null

    /**
     * 订单编号
     */
    @TableField("order_number")
    var orderNumber: String? = null

    /**
     * 项目id
     */
    @TableField("project_id")
    var projectId: Long? = null

    /**
     * 项目名称(可能不需要)
     */
    @TableField("project_name")
    var projectName: String? = null

    /**
     * 结算币种
     */
    @TableField("coin_symbol")
    var coinSymbol: String? = null

    /**
     * 锁仓周期
     */
    @TableField("lock_day")
    var lockDay: Int? = null

    /**
     * 锁仓金额
     */
    @TableField("amount")
    var amount: BigDecimal? = null

    /**
     * 日收益利率-最小
     */
    @TableField("rate_day_limit")
    var rateDayLimit: BigDecimal? = null

    /**
     * 日收益利率-最大
     */
    @TableField("rate_day_max")
    var rateDayMax: BigDecimal? = null

    /**
     * 最小收益
     */
    @TableField("income_limit")
    var incomeLimit: BigDecimal? = null

    /**
     * 最大收益
     */
    @TableField("income_max")
    var incomeMax: BigDecimal? = null

    /**
     * 累计收益
     */
    @TableField("income")
    var income: BigDecimal? = null

    /**
     * 违约结算比例(可能没有)
     */
    @TableField("liquidated_rate")
    var liquidatedRate: BigDecimal? = null

    /**
     * 违约金（可能没有）
     */
    @TableField("liquidated_amount")
    var liquidatedAmount: BigDecimal? = null

    /**
     * 到期时间
     */
    @TableField("expired_time")
    var expiredTime: LocalDateTime? = null

    /**
     * 上一次产生收益时间
     */
    @TableField("last_income_time")
    var lastIncomeTime: LocalDateTime? = null

    /**
     * 产生收益天数
     */
    @TableField("income_days")
    var incomeDays: Int? = null

    /**
     * 状态 （-1：赎回 0：锁仓中，1：结束）
     */
    @TableField("status")
    var status: Int? = null

    /**
     * 失败原因,如果有
     */
    @TableField("reason")
    var reason: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 操作人
     */
    @TableField("update_by")
    var updateBy: String? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppMineOrder{" +
        "id=" + id +
        ", userId=" + userId +
        ", uid=" + uid +
        ", userName=" + userName +
        ", userGroup=" + userGroup +
        ", t1Id=" + t1Id +
        ", t2Id=" + t2Id +
        ", t3Id=" + t3Id +
        ", orderNumber=" + orderNumber +
        ", projectId=" + projectId +
        ", projectName=" + projectName +
        ", coinSymbol=" + coinSymbol +
        ", lockDay=" + lockDay +
        ", amount=" + amount +
        ", rateDayLimit=" + rateDayLimit +
        ", rateDayMax=" + rateDayMax +
        ", incomeLimit=" + incomeLimit +
        ", incomeMax=" + incomeMax +
        ", income=" + income +
        ", liquidatedRate=" + liquidatedRate +
        ", liquidatedAmount=" + liquidatedAmount +
        ", expiredTime=" + expiredTime +
        ", lastIncomeTime=" + lastIncomeTime +
        ", incomeDays=" + incomeDays +
        ", status=" + status +
        ", reason=" + reason +
        ", createTime=" + createTime +
        ", updateBy=" + updateBy +
        ", updateTime=" + updateTime +
        "}"
    }
}
