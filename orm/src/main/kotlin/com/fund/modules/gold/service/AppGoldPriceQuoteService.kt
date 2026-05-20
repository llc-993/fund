package com.fund.modules.gold.service

import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.gold.GoldKlinePeriod
import com.fund.modules.gold.model.AppGoldPriceQuote
import com.fund.modules.gold.mongo.GoldKline
import com.fund.modules.gold.request.GoldQuoteUpsertReq

/** 积存金实时行情 */
interface AppGoldPriceQuoteService : IService<AppGoldPriceQuote> {
    fun upsertQuote(req: GoldQuoteUpsertReq, adminId: Long?): AppGoldPriceQuote
    fun getRealtime(channelId: Long): AppGoldPriceQuote?
    fun getRealtimeByCode(channelCode: String): AppGoldPriceQuote?
    fun listKline(channelCode: String, period: GoldKlinePeriod): List<GoldKline>
}
