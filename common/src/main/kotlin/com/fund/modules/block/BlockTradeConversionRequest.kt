package com.fund.modules.block

import java.io.Serializable
import java.math.BigDecimal

/**
 * 大宗交易转持仓请求
 */
class BlockTradeConversionRequest : Serializable {

    /**
     * 申购记录ID
     */
    var id: Long? = null

    /**
     * 确认数量
     */
    var confirmQuantity: BigDecimal? = null
}

