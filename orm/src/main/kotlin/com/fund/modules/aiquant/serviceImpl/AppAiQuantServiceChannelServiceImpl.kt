package com.fund.modules.aiquant.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.exception.BusinessException
import com.fund.modules.aiquant.mapper.AppAiQuantServiceChannelMapper
import com.fund.modules.aiquant.model.AppAiQuantServiceChannel
import com.fund.modules.aiquant.request.AiQuantChannelPageReq
import com.fund.modules.aiquant.request.AiQuantChannelSaveReq
import com.fund.modules.aiquant.service.AppAiQuantServiceChannelService
import org.springframework.stereotype.Service

@Service
open class AppAiQuantServiceChannelServiceImpl :
    ServiceImpl<AppAiQuantServiceChannelMapper, AppAiQuantServiceChannel>(),
    AppAiQuantServiceChannelService {

    override fun managePage(req: AiQuantChannelPageReq): Page<AppAiQuantServiceChannel> {
        val page = Page<AppAiQuantServiceChannel>(req.current, req.size)
        return page(page, KtQueryWrapper(AppAiQuantServiceChannel()).orderByAsc(AppAiQuantServiceChannel::sortOrder))
    }

    override fun upsert(req: AiQuantChannelSaveReq): AppAiQuantServiceChannel {
        val entity = AppAiQuantServiceChannel().apply {
            id = req.id
            name = req.name
            stockId = req.stockId
            symbol = req.symbol
            market = req.market
            csLink = req.csLink
            sortOrder = req.sortOrder
            enable = req.enable
            remark = req.remark
        }
        if (req.id == null) {
            if (!save(entity)) throw BusinessException("新增渠道失败")
        } else {
            if (!updateById(entity)) throw BusinessException("更新渠道失败")
        }
        return getById(entity.id!!)!!
    }
}
