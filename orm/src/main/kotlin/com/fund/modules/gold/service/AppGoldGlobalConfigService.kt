package com.fund.modules.gold.service

import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.gold.model.AppGoldGlobalConfig
import com.fund.modules.gold.request.GoldGlobalConfigUpdateReq

/** 积存金全局配置 */
interface AppGoldGlobalConfigService : IService<AppGoldGlobalConfig> {
    fun loadOrCreate(): AppGoldGlobalConfig
    fun patch(req: GoldGlobalConfigUpdateReq): AppGoldGlobalConfig
}
