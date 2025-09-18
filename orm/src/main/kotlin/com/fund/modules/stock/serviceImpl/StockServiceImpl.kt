package com.fund.modules.stock.serviceImpl;


import cn.hutool.core.bean.BeanUtil
import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.modules.stock.model.Stock;
import com.fund.modules.stock.mapper.StockMapper;
import com.fund.modules.stock.service.StockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.common.RedisKeys.STOCK_KEY
import com.fund.common.entity.R
import com.fund.modules.stock.QueryStockRequest
import com.fund.modules.stock.service.StockDataRedisService
import com.fund.modules.stock.util.StockDataUtil
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
    private val redissonClient: RedissonClient
) : ServiceImpl<StockMapper, Stock>(), StockService {

    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var stockDataRedisService: StockDataRedisService

    @Autowired
    private lateinit var stockDataUtil: StockDataUtil

    @Transactional(rollbackFor = [Exception::class])
    override fun upsertById(stock: Stock): Boolean {
        return try {
            val bucket = redissonClient.getBucket<String>(STOCK_KEY + stock.flag + stock.symbol)
            if (bucket == null) {
                this.save(stock)
            }
            /*// 查找现有记录
            val existing = this.getOne(
                KtQueryWrapper(Stock())
                    .eq(Stock::symbol, stock.symbol)
                    .eq(Stock::flag, stock.flag)
            )

            val result = if (existing != null) {
                // 更新现有记录，保留ID
                stock.id = existing.id
                this.updateById(stock)
            } else {
                // 创建新记录
                this.save(stock)
            }*/
            bucket.set(JSON.toJSONString(stock))
            true
        } catch (e: Exception) {
            logger.error(e) { "Error upserting stock: symbol=${stock.symbol}" }
            false
        }
    }


    override fun list(req: QueryStockRequest): R<Any> {
        val page: Page<Stock> = Page(req.pageSize, req.pageNum)

        val page1 = this.page(
            page, KtQueryWrapper(Stock())
                .eq(StringUtils.isNotBlank(req.flag), Stock::flag, req.flag)
                .like(StringUtils.isNotBlank(req.symbol), Stock::symbol, req.symbol)
                .orderByDesc(Stock::symbol)
        )

        for (stock in page1.records) {
            // 优先从StockData Redis获取完整信息
            val stockData = stockDataUtil.getFullStockData(stock)
            if (stockData != null) {
                // 使用StockData的完整信息丰富Stock对象
                stockDataUtil.enrichStockFromStockData(stockData, stock)
            } else {
                // 如果StockData不存在，尝试从基本Stock缓存获取
                val bucket = redissonClient.getBucket<String>(STOCK_KEY + stock.flag + stock.id)
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
        val stock = this.getById(stockId)
        val bucket = redissonClient.getBucket<String>(STOCK_KEY + stock.flag + stock.symbol)
        if (bucket.isExists) {
            val s = bucket.get()
            val stock1 = JSON.parseObject(s, Stock::class.java)
            return stock1
        }
        return stock
    }
}
