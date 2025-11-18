package com.fund.modules.ipo

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * IPO修改请求
 */
@Schema(description = "后台更新IPO请求参数")
class AdminIpoUpdateRequest : Serializable {

    @Schema(description = "IPO主键ID", example = "1", required = true)
    var id: Long? = null

    @Schema(description = "股票名称", example = "苹果公司", nullable = true)
    var name: String? = null

    @Schema(description = "国家/地区", example = "US", nullable = true)
    var country: String? = null

    @Schema(description = "股票代码/交易对符号", example = "AAPL", nullable = true)
    var symbol: String? = null

    @Schema(description = "申购开始时间（毫秒时间戳）", example = "2025-01-05T14:30:00", nullable = true)
    var openDate: LocalDateTime? = null

    @Schema(description = "申购结束时间（毫秒时间戳）", example = "2025-01-05T14:30:00", nullable = true)
    var closeDate: LocalDateTime? = null

    @Schema(description = "上市时间（毫秒时间戳）", example = "2025-01-05T14:30:00", nullable = true)
    var listingDate: LocalDateTime? = null

    @Schema(description = "发行价格", example = "150.00", nullable = true)
    var price: BigDecimal? = null

    @Schema(description = "认购数量", example = "1000000", nullable = true)
    var count: Long? = null

    @Schema(description = "交易所名称", example = "NASDAQ", nullable = true)
    var exchange: String? = null

    @Schema(
        description = "IPO状态：1=认购中，2=已结束",
        example = "1",
        allowableValues = ["1", "2"],
        nullable = true
    )
    var status: Int? = null

    @Schema(
        description = "IPO类型：1=新股，2=线下配售，3=定额配售，4=IPO多配",
        example = "1",
        allowableValues = ["1", "2", "3", "4"],
        defaultValue = "1"
    )
    var type: Int? = null

    @Schema(
        description = "转持仓是否锁仓：0=未锁仓，1=锁仓",
        example = "0",
        allowableValues = ["0", "1"],
        defaultValue = "0"
    )
    var isLock: Int = 0

    @Schema(description = "认缴时间", example = "2025-01-05T14:30:00", nullable = true)
    var subscriptionTime: LocalDateTime? = null
}

