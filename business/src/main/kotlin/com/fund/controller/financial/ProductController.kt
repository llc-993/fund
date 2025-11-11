package com.fund.controller.financial

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.modules.financial.model.FinancialProduct
import com.fund.modules.financial.service.FinancialProductService
import com.fund.utils.I18nUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema

@Tag(name = "理财产品", description = "理财产品信息查询接口")
@RestController
@RequestMapping("/financial/product")
class ProductController(
    private val financialProductService: FinancialProductService,
    private val i18nUtil: I18nUtil,
) {

    @Operation(
        summary = "获取理财产品列表",
        description = "查询已上架的理财产品，默认按热门和排序字段倒序排列"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = FinancialProduct::class))])
    @GetMapping("/list")
    fun productList(): R<List<FinancialProduct>> {
        val products = financialProductService.list(
            KtQueryWrapper(FinancialProduct())
                .eq(FinancialProduct::status, 1)
                .orderByDesc(FinancialProduct::isHot)
                .orderByDesc(FinancialProduct::sort)
        )
        return R.success(products)
    }

    @Operation(
        summary = "获取理财产品详情",
        description = "根据产品ID获取理财产品的详细信息"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",content = [Content(schema = Schema(implementation = FinancialProduct::class))])
    @GetMapping("/info/{id}")
    fun productInfo(
        @Parameter(description = "理财产品ID", required = true)
        @PathVariable id: Long
    ): R<FinancialProduct> {
        val product = financialProductService.getById(id)
            ?: return R.error(i18nUtil.getMessage("financial_product_not_exists"))
        return R.success(product)
    }
}