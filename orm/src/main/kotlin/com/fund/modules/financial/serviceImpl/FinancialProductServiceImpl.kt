package com.fund.modules.financial.serviceImpl

import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.exception.BusinessException
import com.fund.modules.financial.FinancialProductCreateRequest
import com.fund.modules.financial.FinancialProductOfflineRequest
import com.fund.modules.financial.FinancialProductUpdateRequest
import com.fund.modules.financial.mapper.FinancialProductMapper
import com.fund.modules.financial.model.FinancialProduct
import com.fund.modules.financial.service.FinancialOrderService
import com.fund.modules.financial.service.FinancialProductService
import mu.KotlinLogging
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


/**
 * <p>
 * 理财产品信息表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
@Service
open class FinancialProductServiceImpl(
    @Lazy private val financialOrderService: FinancialOrderService
) : ServiceImpl<FinancialProductMapper, FinancialProduct>(), FinancialProductService {

    private val logger = KotlinLogging.logger {}

    companion object {
        private const val PRODUCT_STATUS_OFFLINE: Byte = 0
        private const val PRODUCT_STATUS_ONLINE: Byte = 1
    }

    override fun pageQuery(
        pageNum: Int,
        pageSize: Int,
        status: Byte?,
        title: String?,
        productCode: String?
    ): Page<FinancialProduct> {
        val page = Page<FinancialProduct>(pageNum.toLong(), pageSize.toLong())

        val wrapper = KtQueryWrapper(FinancialProduct())
            .eq(status != null, FinancialProduct::status, status)
            .eq(StrUtil.isNotBlank(title), FinancialProduct::title, title)
            .eq(StrUtil.isNotBlank(productCode), FinancialProduct::productCode, productCode)
            .orderByDesc(FinancialProduct::isHot)
            .orderByDesc(FinancialProduct::sort)
            .orderByDesc(FinancialProduct::id)

        return this.page(page, wrapper)
    }

    @Transactional
    override fun createProduct(request: FinancialProductCreateRequest): FinancialProduct {
        // 检查产品编码是否已存在
        val existingProduct = this.getOne(
            KtQueryWrapper(FinancialProduct())
                .eq(FinancialProduct::productCode, request.productCode)
        )

        if (existingProduct != null) {
            throw BusinessException("产品编码已存在")
        }

        // 创建新产品（默认下架状态）
        val product = FinancialProduct().apply {
            this.productCode = request.productCode
            this.title = request.title
            this.iconUrl = request.iconUrl
            this.status = PRODUCT_STATUS_OFFLINE // 默认下架状态
            this.days = request.days
            this.rateType = request.rateType
            this.defaultRate = request.defaultRate
            this.minRate = request.minRate
            this.maxRate = request.maxRate
            this.timeLimit = request.timeLimit
            this.limitMinAmount = request.limitMinAmount
            this.limitMaxAmount = request.limitMaxAmount
            this.coin = request.coin
            this.classify = request.classify
            this.isHot = request.isHot
            this.sort = request.sort
            this.level = request.level
            this.basicInvestAmount = request.basicInvestAmount
            this.totalInvestAmount = request.totalInvestAmount
            this.purchasedAmount = BigDecimal.ZERO
            this.remainAmount = request.totalInvestAmount
            this.avgRate = request.defaultRate
            this.buyPurchase = 0L
            this.remark = request.remark
            this.productIntro = request.productIntro
            this.faq = request.faq
            this.platformRiskRate = request.platformRiskRate
            this.dailyRate = request.dailyRate
        }

        // 保存产品
        this.save(product)
        logger.info { "创建理财产品成功: id=${product.id}, code=${product.productCode}" }

        return product
    }

    @Transactional
    override fun updateProduct(request: FinancialProductUpdateRequest): FinancialProduct {
        // 查询产品
        val productId = request.id
        val product = this.getById(productId) ?: throw BusinessException("产品不存在")

        // 检查产品状态，只有下架状态才能修改
        if (product.status == PRODUCT_STATUS_ONLINE) {
            throw BusinessException("产品已上架，请先下架再修改")
        }

        // 更新产品信息
        request.title?.let { product.title = it }
        request.iconUrl?.let { product.iconUrl = it }
        request.days?.let { product.days = it }
        request.rateType?.let { product.rateType = it }
        request.defaultRate?.let { product.defaultRate = it }
        request.minRate?.let { product.minRate = it }
        request.maxRate?.let { product.maxRate = it }
        request.timeLimit?.let { product.timeLimit = it }
        request.limitMinAmount?.let { product.limitMinAmount = it }
        request.limitMaxAmount?.let { product.limitMaxAmount = it }
        request.coin?.let { product.coin = it }
        request.classify?.let { product.classify = it }
        request.isHot?.let { product.isHot = it }
        request.sort?.let { product.sort = it }
        request.level?.let { product.level = it }
        request.basicInvestAmount?.let { product.basicInvestAmount = it }
        request.totalInvestAmount?.let {
            product.totalInvestAmount = it
            product.remainAmount = it.subtract(product.purchasedAmount ?: BigDecimal.ZERO)
        }
        request.platformRiskRate?.let { product.platformRiskRate = it }
        request.dailyRate?.let { product.dailyRate = it }
        request.productIntro?.let { product.productIntro = it }
        request.faq?.let { product.faq = it }
        request.remark?.let { product.remark = it }

        // 保存更新
        this.updateById(product)
        logger.info { "更新理财产品成功: id=${product.id}" }

        return product
    }

    @Transactional
    override fun onlineProduct(id: Long, remark: String?): FinancialProduct {
        // 查询产品
        val product = this.getById(id) ?: throw BusinessException("产品不存在")

        // 检查产品状态
        if (product.status == PRODUCT_STATUS_ONLINE) {
            throw BusinessException("产品已经是上架状态")
        }

        // 更新产品状态为上架
        product.status = PRODUCT_STATUS_ONLINE
        if (remark != null) {
            product.remark = remark
        }

        // 保存更新
        this.updateById(product)
        logger.info { "上架理财产品成功: id=${product.id}" }

        return product
    }

    @Transactional
    override fun offlineProduct(request: FinancialProductOfflineRequest): FinancialProduct {
        // 查询产品
        val productId = request.id
        val product = this.getById(productId) ?: throw BusinessException("产品不存在")

        // 检查产品状态
        if (product.status == PRODUCT_STATUS_OFFLINE) {
            throw BusinessException("产品已经是下架状态")
        }

        // 更新产品状态为下架
        product.status = PRODUCT_STATUS_OFFLINE
        if (request.remark != null) {
            product.remark = request.remark
        }

        // 如果需要强制赎回
        if (request.forceRedeemAllOrders) {
            // 强制赎回该产品下所有订单
            val redeemCount = financialOrderService.forceRedeemByProductId(productId, request.remark)
            logger.info { "产品 $productId 下架并强制赎回了 $redeemCount 笔订单" }
        }

        // 保存更新
        this.updateById(product)
        logger.info { "下架理财产品成功: id=${product.id}" }

        return product
    }
}