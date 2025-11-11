package com.fund.modules.risingFalling.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 涨跌板块
 * </p>
 *
 * @author 书记
 * @since 2025-10-17
 */
@Schema(description = "涨跌板块配置")
@TableName("rising_falling_sectors")
class RisingFallingSectors : Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "板块主键ID", example = "1001", nullable = true)
    var id: Long? = null

    /**
     * 交易对
     */
    @TableField("symbol")
    @Schema(description = "关联的交易对/股票代码", example = "CN600519", nullable = true)
    var symbol: String? = null

    /**
     * stock 的id
     */
    @TableField("stock_id")
    @Schema(description = "关联股票ID", example = "20001", nullable = true)
    var stockId: Long? = null

    /**
     * 股票锁定状态
     */
    @TableField("stock_lock_status")
    @Schema(description = "股票锁定状态（0=未锁定，1=已锁定）", example = "0", nullable = true)
    var stockLockStatus: Int? = null

    /**
     * 显示状态(0:显示，1:隐藏)
     */
    @TableField("display_status")
    @Schema(description = "展示状态（0=显示，1=隐藏）", example = "0", nullable = true)
    var displayStatus: Int? = null

    /**
     * 开始时间
     */
    @TableField("open_time")
    @Schema(description = "板块开盘时间", example = "2025-10-17T09:30:00", nullable = true)
    var openTime: LocalDateTime? = null

    /**
     * 结束时间
     */
    @TableField("end_time")
    @Schema(description = "板块结束时间", example = "2025-10-17T15:00:00", nullable = true)
    var endTime: LocalDateTime? = null

    /**
     * 开始售卖时间
     */
    @TableField("start_sell_time")
    @Schema(description = "开始售卖时间", example = "2025-10-16T10:00:00", nullable = true)
    var startSellTime: LocalDateTime? = null

    /**
     * 结束售卖的时间
     */
    @TableField("end_sell_time")
    @Schema(description = "结束售卖时间", example = "2025-10-17T08:00:00", nullable = true)
    var endSellTime: LocalDateTime? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "记录创建时间", example = "2025-10-15T12:00:00", nullable = true)
    var createTime: LocalDateTime? = null

    @TableField(value = "pass_word")
    @Schema(description = "板块访问密码（如需校验）", example = "abcd1234", nullable = true)
    var passWord: String? = null

    override fun toString(): String {
        return "RisingFallingSectors(id=$id, symbol=$symbol, stockId=$stockId, stockLockStatus=$stockLockStatus, displayStatus=$displayStatus, openTime=$openTime, endTime=$endTime, startSellTime=$startSellTime, endSellTime=$endSellTime, createTime=$createTime, passWord=$passWord)"
    }


}
