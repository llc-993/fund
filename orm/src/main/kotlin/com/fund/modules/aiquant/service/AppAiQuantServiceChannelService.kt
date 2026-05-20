package com.fund.modules.aiquant.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.fund.modules.aiquant.model.AppAiQuantServiceChannel
import com.fund.modules.aiquant.request.AiQuantChannelPageReq
import com.fund.modules.aiquant.request.AiQuantChannelSaveReq

interface AppAiQuantServiceChannelService : IService<AppAiQuantServiceChannel> {

    fun managePage(req: AiQuantChannelPageReq): Page<AppAiQuantServiceChannel>

    fun upsert(req: AiQuantChannelSaveReq): AppAiQuantServiceChannel
}
