package com.fund.modules.risingfalling

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.time.LocalDateTime

/**
 * 涨跌板块修改请求
 */
@Schema(description = "后台更新涨跌板块请求参数")
class AdminRisingFallingSectorsUpdateRequest : Serializable {

    @Schema(description = "涨跌板块主键ID", example = "1", required = true)
    var id: Long? = null

    @Schema(description = "交易对符号", example = "AAPL", nullable = true)
    var symbol: String? = null

    @Schema(description = "关联的股票ID", example = "1001", nullable = true)
    var stockId: Long? = null

    @Schema(
        description = "股票锁定状态：0=未锁定，1=锁定",
        example = "0",
        allowableValues = ["0", "1"],
        nullable = true
    )
    var stockLockStatus: Int? = null

    @Schema(
        description = "显示状态：0=显示，1=隐藏",
        example = "0",
        allowableValues = ["0", "1"],
        nullable = true
    )
    var displayStatus: Int? = null

    @Schema(description = "开始时间", example = "2025-01-01T00:00:00", nullable = true)
    var openTime: LocalDateTime? = null

    @Schema(description = "结束时间", example = "2025-12-31T23:59:59", nullable = true)
    var endTime: LocalDateTime? = null

    @Schema(description = "开始售卖时间", example = "2025-01-01T09:00:00", nullable = true)
    var startSellTime: LocalDateTime? = null

    @Schema(description = "结束售卖时间", example = "2025-12-31T18:00:00", nullable = true)
    var endSellTime: LocalDateTime? = null

    @Schema(description = "密码", example = "password123", nullable = true)
    var password: String? = null
}
