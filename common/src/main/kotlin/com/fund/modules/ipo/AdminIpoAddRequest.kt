package com.fund.modules.ipo

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * IPO新增请求
 */
@Schema(description = "后台新增IPO请求参数")
class AdminIpoAddRequest : Serializable {

    @Schema(description = "股票名称", example = "苹果公司", required = true)
    var name: String? = null

    @Schema(description = "国家/地区", example = "US", required = true)
    var country: String? = null

    @Schema(description = "股票代码/交易对符号", example = "AAPL", required = true)
    var symbol: String? = null

    @Schema(description = "申购开始时间", example = "2025-01-05T14:30:00", required = true)
    var openDate: LocalDateTime? = null

    @Schema(description = "申购结束时间", example = "2025-01-05T14:30:00", required = true)
    var closeDate: LocalDateTime? = null

    @Schema(description = "上市时间", example = "2025-01-05T14:30:00", required = true)
    var listingDate: LocalDateTime? = null

    @Schema(description = "发行价格", example = "150.00", required = true)
    var price: BigDecimal? = null

    @Schema(description = "认购数量", example = "1000000", required = true)
    var count: Long? = null

    @Schema(description = "交易所名称", example = "NASDAQ", required = true)
    var exchange: String? = null

    @Schema(
        description = "IPO状态：1=认购中，2=已结束",
        example = "1",
        allowableValues = ["1", "2"],
        required = true
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

