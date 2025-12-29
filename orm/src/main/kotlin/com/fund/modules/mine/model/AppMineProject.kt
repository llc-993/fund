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
 * 锁仓挖矿项目
 * </p>
 *
 * @author 书记
 * @since 2025-12-29
 */
@TableName("app_mine_project")
class AppMineProject : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 项目名称(可能不需要)
     */
    @TableField("name")
    var name: String? = null

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
     * 单笔购买金额-最小
     */
    @TableField("amount_min")
    var amountMin: BigDecimal? = null

    /**
     * 单笔购买金额-最大
     */
    @TableField("amount_max")
    var amountMax: BigDecimal? = null

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
     * 违约结算比例（可能没有）
     */
    @TableField("liquidated_rate")
    var liquidatedRate: BigDecimal? = null

    /**
     * 启用
     */
    @TableField("enable")
    var enable: Boolean? = null

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
        return "AppMineProject{" +
        "id=" + id +
        ", name=" + name +
        ", coinSymbol=" + coinSymbol +
        ", lockDay=" + lockDay +
        ", amountMin=" + amountMin +
        ", amountMax=" + amountMax +
        ", rateDayLimit=" + rateDayLimit +
        ", rateDayMax=" + rateDayMax +
        ", liquidatedRate=" + liquidatedRate +
        ", enable=" + enable +
        ", createTime=" + createTime +
        ", updateBy=" + updateBy +
        ", updateTime=" + updateTime +
        "}"
    }
}
