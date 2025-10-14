package com.fund.modules.ipo

import com.fund.common.entity.PageReq
import java.io.Serializable


class AdminIpoQueryRequest: PageReq(), Serializable {

    /**
     * 股票代码
     */
    var symbol: String? = null

    /**
     * 股票名称
     */
    var name: String? = null
}