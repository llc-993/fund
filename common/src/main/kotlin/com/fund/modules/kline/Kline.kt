package com.fund.modules.kline

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.annotation.Id
import java.math.BigDecimal

@Schema(description = "K 线数据点信息")
class Kline {

    @Id
    @Schema(description = "主键ID", example = "1", nullable = true)
    var id: Long? = null

    @Schema(description = "股票或交易对符号", example = "AAPL", nullable = true)
    var symbol: String? = null

    @Schema(description = "K线周期，例如 1min、1h、1day", example = "1h", nullable = true)
    var period: String? = null

    @Schema(description = "数据来源名称", example = "investing", nullable = true)
    var dataSource: String? = null

    @Schema(description = "K线时间（ISO 字符串）", example = "2024-01-01T10:00:00", nullable = true)
    var dateTimeStr: String? = null

    @Schema(description = "成交额/交易额", example = "123456.78", nullable = true)
    var volume: BigDecimal? = null

    @Schema(description = "成交笔数", example = "1200", nullable = true)
    var count: Int? = null

    @Schema(description = "开盘价", example = "145.25", nullable = true)
    var open: BigDecimal? = null

    @Schema(description = "收盘价", example = "147.80", nullable = true)
    var close: BigDecimal? = null

    @Schema(description = "最低价", example = "143.10", nullable = true)
    var low: BigDecimal? = null

    @Schema(description = "最高价", example = "149.60", nullable = true)
    var high: BigDecimal? = null

    @Schema(description = "成交量", example = "890000.00", nullable = true)
    var vol: BigDecimal? = null

    @Schema(description = "数据来源编号", example = "1", nullable = true)
    var source: Int? = 0
}
