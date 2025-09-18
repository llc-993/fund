package com.fund.modules.stock.vo

import java.math.BigDecimal

class PositionInfoVo {


    //关联订单id
     var id: Int? = null

    //用户ID
     var userId: Int? = null

    //股票类型
     var stockType: String? = null

    //股票code
     var stockCode: String? = null

    //开仓价格
     var buyPrice: BigDecimal? = null

    //购买手数
     var orderNum: Int? = null

    //杠杆
     var orderLever: Int? = null

    //每点浮动价
     var eachPoint: BigDecimal? = null

    //每标准手规格
     var lotUnit: Int? = null

    //1-买涨 2-买跌
     var buyType: String? = null

    //当前价
     var nowPrice: BigDecimal? = null

}