package com.fund.modules.stock.serviceImpl;


import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.modules.stock.model.Stock;
import com.fund.modules.stock.mapper.StockMapper;
import com.fund.modules.stock.service.StockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.Constants
import com.fund.common.RedisKeys
import com.fund.common.RedisKeys.STOCK_KEY
import com.fund.common.entity.R
import com.fund.modules.emqt.co.MqttMsg
import com.fund.modules.emqt.service.EmqXService
import com.fund.modules.stock.QueryStockRequest
import com.fund.modules.stock.service.StockDataRedisService
import com.fund.modules.stock.util.StockDataUtil
import io.swagger.v3.core.util.Json
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional
import java.util.Date

/**
 * <p>
 * 股票行情数据表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-08-12
 */
@Service
open class StockServiceImpl(
    private val redissonClient: RedissonClient,
    private val emqXService: EmqXService
) : ServiceImpl<StockMapper, Stock>(), StockService {

    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var stockDataRedisService: StockDataRedisService

    @Autowired
    private lateinit var stockDataUtil: StockDataUtil

    @Transactional(rollbackFor = [Exception::class])
    override fun upsertById(stock: Stock): Boolean {
        return try {
            // 先检查 Redis 是否有数据
            val bucket = redissonClient.getBucket<String>(STOCK_KEY + stock.flag + stock.symbol)
            
            if (bucket.isExists) {
                // Redis 有数据，解析获取缓存的 stock
                val cachedJson = bucket.get()
                val cachedStock = JSON.parseObject(cachedJson, Stock::class.java)
                
                // 保存 cachedStock 的 id，避免被覆盖
                val cachedId = cachedStock.id
                
                // 只复制非空字段，避免覆盖原有数据
                mergeStockFields(stock, cachedStock)
                
                // 恢复 cachedStock 的 id
                cachedStock.id = cachedId
                
                // 更新完整的 cachedStock 到数据库
                this.updateById(cachedStock)
                
                // 将更新后的 cachedStock 复制回 stock，用于后续保存到 Redis
                BeanUtil.copyProperties(cachedStock, stock)
            } else {
                // Redis 没有数据，查找数据库
                val list = this.list(
                    KtQueryWrapper(Stock())
                        .eq(Stock::symbol, stock.symbol)
                        .eq(StrUtil.isNotBlank(stock.flag), Stock::flag, stock.flag)
                        .eq(Stock::sourceType, stock.sourceType)
                )

                if (list.isNotEmpty() && list.size >= 2) {
                    this.removeById(list[1].id)
                }

                if (list.isNotEmpty()) {
                    // 数据库有记录，合并数据后更新
                    val existingStock = list[0]
                    mergeStockFields(stock, existingStock)
                    existingStock.id = list[0].id
                    this.updateById(existingStock)
                    // 将合并后的数据复制回 stock，用于保存到 Redis
                    BeanUtil.copyProperties(existingStock, stock)
                } else if (stock.id == null) {
                    // 数据库没有记录，新增
                    this.save(stock)
                }
            }
            
            // 序列化完整的 Stock 对象到 Redis（包含 null 值）
            bucket.set(JSON.toJSONString(stock, com.alibaba.fastjson2.JSONWriter.Feature.WriteNulls))
            true
        } catch (e: Exception) {
            logger.error(e) { "Error upserting stock: symbol=${stock.symbol}" }
            false
        }
    }


    override fun list(req: QueryStockRequest): R<Any> {
        val page: Page<Stock> = Page(req.pageNum, req.pageSize)

        val page1 = this.page(
            page, KtQueryWrapper(Stock())
                .eq(StringUtils.isNotBlank(req.flag), Stock::flag, req.flag)
                .like(StringUtils.isNotBlank(req.symbol), Stock::symbol, req.symbol)
                .orderByDesc(Stock::chg)
        )

        for (stock in page1.records) {
            // 优先从StockData Redis获取完整信息
            val stockData = stockDataUtil.getFullStockData(stock)
            if (stockData != null) {
                // 使用StockData的完整信息丰富Stock对象
                stockDataUtil.enrichStockFromStockData(stockData, stock)
            } else {
                // 如果StockData不存在，尝试从基本Stock缓存获取
                val bucket = redissonClient.getBucket<String>(STOCK_KEY + stock.flag + stock.symbol)
                if (bucket.isExists) {
                    val s = bucket.get()
                    val stock1 = JSON.parseObject(s, Stock::class.java)
                    BeanUtil.copyProperties(stock1, stock)
                }
            }
        }
        return R.success(page1)
    }

    override fun countryList(): R<Any> {
        val redisCache = redissonClient.getList<String>(RedisKeys.COUNTRY_KEY)

        if (redisCache.isExists) {
            return R.success(redisCache.toSet())
        }

        val list = this.list(
            KtQueryWrapper(Stock())
                .select(Stock::flag)
        )
        val flags = list.mapNotNull { it.flag }
            .distinct()

        redisCache.addAll(flags)

        return R.success(flags)
    }

    override fun getStockById(stockId: Long): Stock {
        return try {
            val stock = this.getById(stockId)

            val bucket = redissonClient.getBucket<String>(STOCK_KEY + stock.flag + stock.symbol)
            if (bucket.isExists) {
                val s = bucket.get()
                val stock1 = JSON.parseObject(s, Stock::class.java)
                stock1.id = stock.id
                BeanUtil.copyProperties(stock1, stock)
                return stock
            }
            stock
        } catch (e: Exception) {
            logger.error(e) { "Error getting stock by id=$stockId" }
            throw e
        }
    }

    override fun loadStockPid2Redis() {
        val list = this.list(
            KtQueryWrapper(Stock())
                .isNotNull(Stock::pId)
        )
        val map = redissonClient.getMap<Long, Long>(RedisKeys.STOCK_PID_KEY)
        for (stock in list) {
            try {
                map.put(stock.pId, stock.id)

                val stock1 = this.getStockById(stock.id!!)

                stock1.id = stock.id
                this.upsertById(stock1)
            } catch (e: Exception) {
                logger.error(e) { "Error upsertting stock by id=${stock.id}" }
                continue
            }

        }
    }

    // 合并股票字段：只更新非空字段，保留原有数据
    private fun mergeStockFields(source: Stock, target: Stock) {
        source.name?.let { target.name = it }
        source.symbol?.let { target.symbol = it }
        source.flag?.let { target.flag = it }
        source.isCfd?.let { target.isCfd = it }
        source.high?.let { target.high = it }
        source.low?.let { target.low = it }
        source.last?.let { target.last = it }
        source.lastPairDecimal?.let { target.lastPairDecimal = it }
        source.chg?.let { target.chg = it }
        source.chgPct?.let { target.chgPct = it }
        source.volume?.let { target.volume = it }
        source.avgVolume?.let { target.avgVolume = it }
        source.time?.let { target.time = it }
        source.isOpen?.let { target.isOpen = it }
        source.url?.let { target.url = it }
        source.countryNameTranslated?.let { target.countryNameTranslated = it }
        source.exchangeId?.let { target.exchangeId = it }
        source.performanceDay?.let { target.performanceDay = it }
        source.performanceWeek?.let { target.performanceWeek = it }
        source.performanceMonth?.let { target.performanceMonth = it }
        source.performanceYtd?.let { target.performanceYtd = it }
        source.performanceYear?.let { target.performanceYear = it }
        source.performance3year?.let { target.performance3year = it }
        source.technicalHour?.let { target.technicalHour = it }
        source.technicalDay?.let { target.technicalDay = it }
        source.technicalWeek?.let { target.technicalWeek = it }
        source.technicalMonth?.let { target.technicalMonth = it }
        source.fundamentalMarketCap?.let { target.fundamentalMarketCap = it }
        source.fundamentalRevenue?.let { target.fundamentalRevenue = it }
        source.fundamentalRatio?.let { target.fundamentalRatio = it }
        source.fundamentalBeta?.let { target.fundamentalBeta = it }
        source.pairType?.let { target.pairType = it }
        source.pId?.let { target.pId = it }
        source.sourceType?.let { target.sourceType = it }
    }
}
