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
 * 锁仓挖矿项目订单收益记录
 * </p>
 *
 * @author 书记
 * @since 2025-12-29
 */
@TableName("app_mine_order_income_log")
class AppMineOrderIncomeLog : Serializable {

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
     * 订单id
     */
    @TableField("order_id")
    var orderId: Long? = null

    /**
     * 累计收益
     */
    @TableField("income")
    var income: BigDecimal? = null

    /**
     * 收益重复KEY值，如果相同就是重复
     */
    @TableField("day_key")
    var dayKey: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppMineOrderIncomeLog{" +
        "id=" + id +
        ", userId=" + userId +
        ", orderId=" + orderId +
        ", income=" + income +
        ", dayKey=" + dayKey +
        ", createTime=" + createTime +
        "}"
    }
}
