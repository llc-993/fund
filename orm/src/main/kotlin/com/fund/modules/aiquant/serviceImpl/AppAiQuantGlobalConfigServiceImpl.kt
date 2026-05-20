package com.fund.modules.aiquant.serviceImpl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.exception.BusinessException
import com.fund.modules.aiquant.mapper.AppAiQuantGlobalConfigMapper
import com.fund.modules.aiquant.model.AppAiQuantGlobalConfig
import com.fund.modules.aiquant.request.AiQuantGlobalConfigUpdateReq
import com.fund.modules.aiquant.service.AppAiQuantGlobalConfigService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
open class AppAiQuantGlobalConfigServiceImpl :
    ServiceImpl<AppAiQuantGlobalConfigMapper, AppAiQuantGlobalConfig>(),
    AppAiQuantGlobalConfigService {

    companion object {
        private const val CONFIG_ID = 1
    }

    override fun loadOrCreate(): AppAiQuantGlobalConfig {
        val existing = getById(CONFIG_ID)
        if (existing != null) {
            return existing
        }
        val row = AppAiQuantGlobalConfig().apply {
            id = CONFIG_ID
            minReserveAmount = BigDecimal.ZERO
            feeRate = BigDecimal.ZERO
            replaceContractEntry = 1
        }
        if (!save(row)) {
            throw BusinessException("初始化AI量化全局配置失败")
        }
        return row
    }

    override fun patch(req: AiQuantGlobalConfigUpdateReq): AppAiQuantGlobalConfig {
        val row = loadOrCreate()
        req.minReserveAmount?.let { row.minReserveAmount = it }
        req.feeRate?.let { row.feeRate = it }
        req.replaceContractEntry?.let { row.replaceContractEntry = it }
        if (!updateById(row)) {
            throw BusinessException("更新全局配置失败")
        }
        return row
    }
}
