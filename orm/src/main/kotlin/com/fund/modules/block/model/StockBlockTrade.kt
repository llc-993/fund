package com.fund.modules.block.model;

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
 * 股票大宗交易
 * </p>
 *
 * @author 书记
 * @since 2025-10-10
 */
@Schema(description = "股票大宗交易配置信息")
@TableName("stock_block_trade")
class StockBlockTrade : Serializable {

    /**
     * 主键
     */
    @Schema(description = "大宗交易ID", example = "1001", nullable = true)
    @TableId(value = "id", type = IdType.AUTO)
    var id: Int? = null

    /**
     * 股票名称
     */
    @Schema(description = "股票名称", example = "贵州茅台", nullable = true)
    @TableField("name")
    var name: String? = null

    /**
     * 股票id
     */
    @Schema(description = "关联的股票ID", example = "600519", nullable = true)
    @TableField("stock_id")
    var stockId: Long? = null

    /**
     * 最大买入数量
     */
    @Schema(description = "最大买入数量", example = "10000", nullable = true)
    @TableField("max_amount")
    var maxAmount: BigDecimal? = null

    /**
     * 最小买入数量
     */
    @Schema(description = "最小买入数量", example = "100", nullable = true)
    @TableField("min_amount")
    var minAmount: BigDecimal? = null

    /**
     * 锁定状态，1:锁定，2:不锁定
     * 如果设置为锁定，转持仓后的股票将被锁定，不能立即卖出
     */
    @Schema(description = "锁定状态（1:锁定，2:不锁定）", example = "1", nullable = true)
    @TableField("lock_status")
    var lockStatus: Int? = null

    /**
     * 状态，1:开放中，2:已关闭
     */
    @Schema(description = "大宗交易状态（1:开放中，2:已关闭）", example = "1", nullable = true)
    @TableField("status")
    var status: Int? = null

    /**
     * 折扣（例如：0.9 表示9折，0.8 表示8折）
     * 最终购买价格 = 股票当前价格 × 折扣
     */
    @Schema(description = "折扣率（例如：0.9表示9折）", example = "0.9", nullable = true)
    @TableField("discount")
    var discount: BigDecimal? = null

    /**
     * 开始售卖时间
     * 在此时间之前不允许申购
     */
    @Schema(description = "开始售卖时间", example = "2025-11-01T09:30:00", nullable = true)
    @TableField("start_date_time")
    var startDateTime: LocalDateTime? = null

    /**
     * 结束售卖时间
     * 在此时间之后不允许申购
     */
    @Schema(description = "结束售卖时间", example = "2025-11-10T15:00:00", nullable = true)
    @TableField("end_date_time")
    var endDateTime: LocalDateTime? = null

    /**
     * 完全释放锁定时间
     * 如果设置了锁定，持仓将在此时间点完全解锁
     */
    @Schema(description = "完全释放锁定时间", example = "2025-12-01T00:00:00", nullable = true)
    @TableField("release_look_time")
    var releaseLookTime: LocalDateTime? = null

    /**
     * 第一次释放比例（例如：0.5 表示释放50%）
     * 在 firstReleaseLookDateTime 时间点，释放此比例的持仓
     */
    @Schema(description = "第一次释放比例（例如：0.5表示释放50%）", example = "0.5", nullable = true)
    @TableField("first_release_look_rate")
    var firstReleaseLookRate: BigDecimal? = null

    /**
     * 第一次释放时间
     * 在此时间点，释放 firstReleaseLookRate 比例的持仓
     * 剩余部分在 releaseLookTime 时间点释放
     */
    @Schema(description = "第一次释放时间", example = "2025-11-20T00:00:00", nullable = true)
    @TableField("first_release_look_date_time")
    var firstReleaseLookDateTime: LocalDateTime? = null

    @Schema(description = "当前股票价格（非持久化字段）", example = "2000.00", nullable = true)
    @TableField(exist = false)
    var price: BigDecimal? = BigDecimal.ZERO

    override fun toString(): String {
        return "StockBlockTrade{" +
        "id=" + id +
        ", name=" + name +
        ", stockId=" + stockId +
        ", maxAmount=" + maxAmount +
        ", minAmount=" + minAmount +
        ", lockStatus=" + lockStatus +
        ", status=" + status +
        ", discount=" + discount +
        ", startDateTime=" + startDateTime +
        ", endDateTime=" + endDateTime +
        ", releaseLookTime=" + releaseLookTime +
        ", firstReleaseLookRate=" + firstReleaseLookRate +
        ", firstReleaseLookDateTime=" + firstReleaseLookDateTime +
        "}"
    }
}
