package com.fund.modules.gold.serviceImpl

import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.common.RedisKeys
import com.fund.exception.BusinessException
import com.fund.modules.gold.GoldKlinePeriod
import com.fund.modules.gold.mapper.AppGoldPriceQuoteMapper
import com.fund.modules.gold.model.AppGoldPriceQuote
import com.fund.modules.gold.mongo.GoldKline
import com.fund.modules.gold.request.GoldQuoteUpsertReq
import com.fund.modules.gold.service.AppGoldChannelService
import com.fund.modules.gold.service.AppGoldGlobalConfigService
import com.fund.modules.gold.service.AppGoldPriceQuoteService
import com.fund.modules.gold.service.GoldKlineMongoService
import com.fund.utils.RedisLockService
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
open class AppGoldPriceQuoteServiceImpl(
    private val redissonClient: RedissonClient,
    private val channelService: AppGoldChannelService,
    private val klineService: GoldKlineMongoService,
    private val globalConfigService: AppGoldGlobalConfigService,
) : ServiceImpl<AppGoldPriceQuoteMapper, AppGoldPriceQuote>(),
    AppGoldPriceQuoteService {

    override fun upsertQuote(req: GoldQuoteUpsertReq, adminId: Long?): AppGoldPriceQuote {
        val lockKey = RedisKeys.LOCK_GOLD_QUOTE + req.channelId
        return RedisLockService.lockTransaction(lockKey) {
            val channel = channelService.getById(req.channelId)
                ?: throw BusinessException("渠道不存在")
            if ((channel.enableFlag ?: 0) != 1) {
                throw BusinessException("渠道已下架，禁止写入行情")
            }
            val now = req.quoteTime ?: LocalDateTime.now()
            val price = req.price.setScale(8, RoundingMode.HALF_UP)
            if (price.signum() <= 0) throw BusinessException("行情价格必须大于0")

            val existing = getOne(
                KtQueryWrapper(AppGoldPriceQuote()).eq(AppGoldPriceQuote::channelId, req.channelId),
            )
            val today = now.toLocalDate()
            val sameDay = existing?.quoteTime?.toLocalDate() == today
            val prevClose = req.prevClosePrice ?: existing?.prevClosePrice ?: price
            val change = price.subtract(prevClose).setScale(8, RoundingMode.HALF_UP)
            val pct = if (prevClose.signum() == 0) {
                BigDecimal.ZERO
            } else {
                change.divide(prevClose, 8, RoundingMode.HALF_UP)
            }
            val open = if (sameDay) existing?.intradayOpen ?: price else price
            val high = if (sameDay) {
                maxOf(existing?.intradayHigh ?: price, price)
            } else {
                price
            }
            val low = if (sameDay) {
                minOf(existing?.intradayLow ?: price, price)
            } else {
                price
            }

            val entity = (existing ?: AppGoldPriceQuote()).apply {
                channelId = req.channelId
                channelCode = channel.channelCode
                this.price = price
                this.prevClosePrice = prevClose
                this.changeAmount = change
                this.changePct = pct
                this.intradayOpen = open
                this.intradayHigh = high
                this.intradayLow = low
                this.quoteTime = now
                this.tradingStatus = req.tradingStatus ?: 1
            }
            if (existing == null) save(entity) else updateById(entity)

            val millis = now.atZone(GoldKlineMongoServiceImpl.HK_ZONE).toInstant().toEpochMilli()
            klineService.appendQuote(channel, price, millis)

            val cfg = globalConfigService.loadOrCreate()
            val ttl = (cfg.quoteCacheSeconds ?: 5).coerceAtLeast(1).toLong()
            val cacheKey = RedisKeys.CACHE_GOLD_QUOTE + req.channelId
            redissonClient.getBucket<String>(cacheKey).set(JSON.toJSONString(entity), ttl, TimeUnit.SECONDS)

            entity
        }
    }

    override fun getRealtime(channelId: Long): AppGoldPriceQuote? {
        val cacheKey = RedisKeys.CACHE_GOLD_QUOTE + channelId
        val bucket = redissonClient.getBucket<String>(cacheKey)
        bucket.get()?.let { json ->
            return try {
                JSON.parseObject(json, AppGoldPriceQuote::class.java)
            } catch (_: Exception) {
                null
            }
        }
        val db = getOne(
            KtQueryWrapper(AppGoldPriceQuote()).eq(AppGoldPriceQuote::channelId, channelId),
        ) ?: return null
        val cfg = globalConfigService.loadOrCreate()
        val ttl = (cfg.quoteCacheSeconds ?: 5).coerceAtLeast(1).toLong()
        bucket.set(JSON.toJSONString(db), ttl, TimeUnit.SECONDS)
        return db
    }

    override fun getRealtimeByCode(channelCode: String): AppGoldPriceQuote? {
        val entity = getOne(
            KtQueryWrapper(AppGoldPriceQuote()).eq(AppGoldPriceQuote::channelCode, channelCode),
        ) ?: return null
        return getRealtime(entity.channelId!!)
    }

    override fun listKline(channelCode: String, period: GoldKlinePeriod): List<GoldKline> =
        klineService.listLatest(channelCode, period.interval, period.limit)
}
