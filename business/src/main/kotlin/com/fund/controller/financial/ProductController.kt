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

@Tag(name = "理财产品", description = "理财产品相关接口")
@RestController
@RequestMapping("/product")
class ProductController(
    private val financialProductService: FinancialProductService,
    private val i18nUtil: I18nUtil,
) {

    @Operation(
        summary = "获取理财产品列表",
        description = "查询可申购理财产品并按排序字段倒序返回"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/list")
    fun list(): R<List<FinancialProduct>> {
        val financialProducts = financialProductService.list(
            KtQueryWrapper(FinancialProduct())
                .eq(FinancialProduct::productStatus, 1)
                .orderByDesc(FinancialProduct::sort)
        )
        return R.success(financialProducts)
    }

    @Operation(
        summary = "获取理财产品详情",
        description = "根据产品ID查询理财产品详细信息"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/info/{id}")
    fun getProduct(
        @Parameter(description = "理财产品ID", required = true)
        @PathVariable id: Long
    ): R<FinancialProduct> {
        val product = financialProductService.getById(id)
            ?: return R.error(i18nUtil.getMessage("product_not_exists_or_offline"))
        return R.success(product)
    }
}