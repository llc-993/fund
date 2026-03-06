package com.fund.modules.quotation.model

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 用户行情调控表
 * </p>
 *
 * @author 书记
 * @since 2026-02-03
 */
@TableName("user_quotation_control")
class UserQuotationControl : Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 用户ID
     */
    @TableField("user_id")
    var userId: Long? = null

    /**
     * 股票代码
     */
    @TableField("symbol")
    var symbol: String? = null

    /**
     * 市场类型(US/CN/IN等)
     */
    @TableField("stock_type")
    var stockType: String? = null

    /**
     * 价格浮动值(正数上浮high,负数下浮low)
     */
    @TableField("floating")
    var floating: BigDecimal? = null

    /**
     * 调控生效时间戳(秒级)
     */
    @TableField("effect_time")
    var effectTime: Long? = null

    /**
     * 是否启用(0=禁用,1=启用)
     */
    @TableField("is_active")
    var isActive: Byte? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

    @TableField("created_at")
    var createdAt: LocalDateTime? = null

    @TableField("updated_at")
    var updatedAt: LocalDateTime? = null

    override fun toString(): String {
        return "UserQuotationControl{" +
        "id=" + id +
        ", userId=" + userId +
        ", symbol=" + symbol +
        ", stockType=" + stockType +
        ", floating=" + floating +
        ", isActive=" + isActive +
        ", remark=" + remark +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        "}"
    }
}
