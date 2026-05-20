package com.fund.modules.gold.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.gold.model.AppGoldChannel
import com.fund.modules.gold.request.GoldChannelPageReq
import com.fund.modules.gold.request.GoldChannelSaveReq
import com.fund.modules.gold.vo.GoldChannelHomeVo

/** 积存金渠道 */
interface AppGoldChannelService : IService<AppGoldChannel> {
    fun listEnabledForUser(): List<GoldChannelHomeVo>
    fun getEnabledById(id: Long): AppGoldChannel?
    fun managePage(req: GoldChannelPageReq): Page<AppGoldChannel>
    fun upsert(req: GoldChannelSaveReq): AppGoldChannel
    fun toggleEnable(id: Long, enable: Int): Boolean
}
