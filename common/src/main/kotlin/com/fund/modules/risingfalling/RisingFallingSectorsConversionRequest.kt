package com.fund.modules.risingFalling

import java.io.Serializable
import java.math.BigDecimal

/**
 * 涨跌板块转持仓请求
 */
class RisingFallingSectorsConversionRequest : Serializable {

    /**
     * 申购记录ID
     */
    var id: Long? = null

    /**
     * 确认数量
     */
    var confirmQuantity: BigDecimal? = null
}
