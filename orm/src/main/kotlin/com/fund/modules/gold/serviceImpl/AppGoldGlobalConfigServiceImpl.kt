package com.fund.modules.gold.serviceImpl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.exception.BusinessException
import com.fund.modules.gold.mapper.AppGoldGlobalConfigMapper
import com.fund.modules.gold.model.AppGoldGlobalConfig
import com.fund.modules.gold.request.GoldGlobalConfigUpdateReq
import com.fund.modules.gold.service.AppGoldGlobalConfigService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
open class AppGoldGlobalConfigServiceImpl :
    ServiceImpl<AppGoldGlobalConfigMapper, AppGoldGlobalConfig>(),
    AppGoldGlobalConfigService {

    companion object {
        private const val CONFIG_ID = 1
    }

    override fun loadOrCreate(): AppGoldGlobalConfig {
        val existing = getById(CONFIG_ID)
        if (existing != null) return existing
        val row = AppGoldGlobalConfig().apply {
            id = CONFIG_ID
            defaultBuyFeeRate = BigDecimal.ZERO
            defaultSellFeeRate = BigDecimal.ZERO
            defaultMinBuyAmount = BigDecimal.ZERO
            defaultMinSellGrams = BigDecimal.ZERO
            defaultGramScale = 4
            defaultPriceToleranceBps = 100
            currencyCode = "HKD"
            quoteCacheSeconds = 5
            entryEnable = 1
        }
        if (!save(row)) throw BusinessException("初始化积存金全局配置失败")
        return row
    }

    override fun patch(req: GoldGlobalConfigUpdateReq): AppGoldGlobalConfig {
        val row = loadOrCreate()
        req.defaultBuyFeeRate?.let { row.defaultBuyFeeRate = it }
        req.defaultSellFeeRate?.let { row.defaultSellFeeRate = it }
        req.defaultMinBuyAmount?.let { row.defaultMinBuyAmount = it }
        req.defaultMinSellGrams?.let { row.defaultMinSellGrams = it }
        req.defaultGramScale?.let { row.defaultGramScale = it }
        req.defaultPriceToleranceBps?.let { row.defaultPriceToleranceBps = it }
        req.quoteCacheSeconds?.let { row.quoteCacheSeconds = it }
        req.riskNoticeUrl?.let { row.riskNoticeUrl = it }
        req.entryEnable?.let { row.entryEnable = it }
        if (!updateById(row)) throw BusinessException("更新积存金全局配置失败")
        return row
    }
}
