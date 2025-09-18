package com.fund.modules.stock

import com.fund.common.entity.PageReq

class QueryStockRequest: PageReq() {

    var symbol: String? = null

    var flag: String? = null

}