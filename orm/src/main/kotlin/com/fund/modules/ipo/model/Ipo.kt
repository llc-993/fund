package com.fund.modules.ipo.model;

import com.baomidou.mybatisplus.annotation.*
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * IPO信息表
 * </p>
 *
 * @author 书记
 * @since 2025-10-07
 */
@Schema(description = "IPO新股申购信息")
@TableName("ipo")
class Ipo : Serializable {

    @Schema(description = "IPO主键ID", example = "1", nullable = true)
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    @Schema(description = "股票名称", example = "苹果公司", nullable = true)
    @TableField("name")
    var name: String? = null

    @Schema(description = "国家/地区", example = "US", nullable = true)
    @TableField("country")
    var country: String? = null

    @Schema(description = "股票代码/交易对符号", example = "AAPL", nullable = true)
    @TableField("symbol")
    var symbol: String? = null

    @Schema(description = "申购开始时间", example = "2025-01-01T00:00:00", nullable = true)
    @TableField("open_date")
    var openDate: LocalDateTime? = null

    @Schema(description = "申购结束时间", example = "2025-01-10T23:59:59", nullable = true)
    @TableField("close_date")
    var closeDate: LocalDateTime? = null

    @Schema(description = "上市时间", example = "2025-01-15T09:30:00", nullable = true)
    @TableField("listing_date")
    var listingDate: LocalDateTime? = null

    @Schema(description = "发行价格", example = "150.00", nullable = true)
    @TableField("price")
    var price: BigDecimal? = null

    @Schema(description = "认购数量", example = "1000000", nullable = true)
    @TableField("count")
    var count: Long? = null

    @Schema(description = "交易所名称", example = "NASDAQ", nullable = true)
    @TableField("exchange")
    var exchange: String? = null

    @Schema(
        description = "IPO状态：1=认购中，2=已结束",
        example = "1",
        allowableValues = ["1", "2"],
        nullable = true
    )
    @TableField("status")
    var status: Int? = null

    @Schema(
        description = "IPO类型：1=新股，2=线下配售，3=定额配售，4=IPO多配",
        example = "1",
        allowableValues = ["1", "2", "3", "4"],
        defaultValue = "1"
    )
    @TableField("type")
    var type: Int? = null

    @Schema(description = "创建时间", example = "2025-01-01T10:00:00", nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    @Schema(
        description = "转持仓是否锁仓：0=未锁仓，1=锁仓",
        example = "0",
        allowableValues = ["0", "1"],
        defaultValue = "0"
    )
    @TableField(value = "is_lock")
    var isLock: Int = 0

    @Schema(description = "认缴时间", example = "2025-01-05T14:30:00", nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "subscription_time")
    var subscriptionTime: LocalDateTime? = null

    override fun toString(): String {
        return "Ipo{" +
                "id=" + id +
                ", name=" + name +
                ", country=" + country +
                ", symbol=" + symbol +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", listingDate=" + listingDate +
                ", price=" + price +
                ", count=" + count +
                ", exchange=" + exchange +
                ", status=" + status +
                ", createTime=" + createTime +
                ", isLock=" + isLock +
                ", subscriptionTime=" + subscriptionTime +
                "}"
    }
}
