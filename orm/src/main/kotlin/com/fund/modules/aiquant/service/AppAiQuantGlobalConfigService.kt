package com.fund.modules.aiquant.service

import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.aiquant.model.AppAiQuantGlobalConfig
import com.fund.modules.aiquant.request.AiQuantGlobalConfigUpdateReq

/**
 * AI 量化全局配置（单行）
 */
interface AppAiQuantGlobalConfigService : IService<AppAiQuantGlobalConfig> {

    /** 若不存在首行则插入默认记录 */
    fun loadOrCreate(): AppAiQuantGlobalConfig

    fun patch(req: AiQuantGlobalConfigUpdateReq): AppAiQuantGlobalConfig
}
