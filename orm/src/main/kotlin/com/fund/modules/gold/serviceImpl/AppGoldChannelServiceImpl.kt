package com.fund.modules.gold.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.exception.BusinessException
import com.fund.modules.gold.mapper.AppGoldChannelMapper
import com.fund.modules.gold.mapper.AppGoldPriceQuoteMapper
import com.fund.modules.gold.model.AppGoldChannel
import com.fund.modules.gold.model.AppGoldPriceQuote
import com.fund.modules.gold.request.GoldChannelPageReq
import com.fund.modules.gold.request.GoldChannelSaveReq
import com.fund.modules.gold.service.AppGoldChannelService
import com.fund.modules.gold.vo.GoldChannelHomeVo
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
open class AppGoldChannelServiceImpl(
    private val priceQuoteMapper: AppGoldPriceQuoteMapper,
) : ServiceImpl<AppGoldChannelMapper, AppGoldChannel>(),
    AppGoldChannelService {

    override fun listEnabledForUser(): List<GoldChannelHomeVo> {
        val channels = list(
            KtQueryWrapper(AppGoldChannel())
                .eq(AppGoldChannel::enableFlag, 1)
                .orderByDesc(AppGoldChannel::sortOrder),
        )
        return channels.map { ch ->
            val quote = priceQuoteMapper.selectOne(
                KtQueryWrapper(AppGoldPriceQuote()).eq(AppGoldPriceQuote::channelId, ch.id),
            )
            GoldChannelHomeVo(
                channelId = ch.id!!,
                channelCode = ch.channelCode ?: "",
                channelName = ch.channelName ?: "",
                bankName = ch.bankName,
                accountLabel = ch.accountLabel,
                accountTag = ch.accountTag,
                logoUrl = ch.logoUrl,
                csLink = ch.csLink,
                currencyCode = ch.currencyCode ?: "HKD",
                gramScale = ch.gramScale ?: 4,
                price = quote?.price ?: BigDecimal.ZERO,
                changeAmount = quote?.changeAmount ?: BigDecimal.ZERO,
                changePct = quote?.changePct ?: BigDecimal.ZERO,
                tradingStatus = quote?.tradingStatus ?: 0,
                intradayHigh = quote?.intradayHigh,
                intradayLow = quote?.intradayLow,
                intradayOpen = quote?.intradayOpen,
            )
        }
    }

    override fun getEnabledById(id: Long): AppGoldChannel? {
        val ch = getById(id) ?: return null
        return if ((ch.enableFlag ?: 0) == 1) ch else null
    }

    override fun managePage(req: GoldChannelPageReq): Page<AppGoldChannel> {
        val page = Page<AppGoldChannel>(req.current, req.size)
        val w = KtQueryWrapper(AppGoldChannel()).orderByDesc(AppGoldChannel::sortOrder)
        req.enableFlag?.let { w.eq(AppGoldChannel::enableFlag, it) }
        req.channelCode?.takeIf { it.isNotBlank() }?.let { w.eq(AppGoldChannel::channelCode, it) }
        return page(page, w)
    }

    override fun upsert(req: GoldChannelSaveReq): AppGoldChannel {
        val entity = AppGoldChannel().apply {
            id = req.id
            channelCode = req.channelCode
            channelName = req.channelName
            bankName = req.bankName
            accountLabel = req.accountLabel
            accountTag = req.accountTag
            logoUrl = req.logoUrl
            csLink = req.csLink
            riskNoticeUrl = req.riskNoticeUrl
            currencyCode = req.currencyCode ?: "HKD"
            buyFeeRate = req.buyFeeRate
            sellFeeRate = req.sellFeeRate
            minBuyAmount = req.minBuyAmount
            minSellGrams = req.minSellGrams
            gramScale = req.gramScale
            priceToleranceBps = req.priceToleranceBps
            sortOrder = req.sortOrder
            enableFlag = req.enableFlag
            remark = req.remark
        }
        if (req.id == null) {
            if (!save(entity)) throw BusinessException("新增渠道失败")
        } else {
            if (!updateById(entity)) throw BusinessException("更新渠道失败")
        }
        return getById(entity.id!!)!!
    }

    override fun toggleEnable(id: Long, enable: Int): Boolean {
        val ch = getById(id) ?: throw BusinessException("渠道不存在")
        ch.enableFlag = enable
        return updateById(ch)
    }
}
