package com.fund.controller.financial

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.modules.financial.FinancialProductCreateRequest
import com.fund.modules.financial.FinancialProductOfflineRequest
import com.fund.modules.financial.FinancialProductUpdateRequest
import com.fund.modules.financial.model.FinancialProduct
import com.fund.modules.financial.service.FinancialProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "理财产品管理", description = "理财产品管理相关接口")
@RestController
@RequestMapping("/financial/product/manage")
class ProductManageController(
    private val financialProductService: FinancialProductService
) {

    @Operation(
        summary = "查询理财产品列表",
        description = "分页查询理财产品列表，可按状态筛选"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "查询成功", 
        content = [Content(schema = Schema(implementation = FinancialProduct::class))]
    )
    @GetMapping("/list")
    fun list(
        @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") pageNum: Int,
        @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") pageSize: Int,
        @Parameter(description = "状态：1-上架 0-下架") @RequestParam(required = false) status: Byte?
    ): R<Page<FinancialProduct>> {
        val page = financialProductService.pageQuery(pageNum, pageSize, status)
        return R.success(page)
    }
    
    @Operation(
        summary = "创建理财产品",
        description = "创建新的理财产品，默认为下架状态"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "创建成功", 
        content = [Content(schema = Schema(implementation = FinancialProduct::class))]
    )
    @PostMapping("/create")
    fun create(@RequestBody request: FinancialProductCreateRequest): R<FinancialProduct> {
        val product = financialProductService.createProduct(request)
        return R.success(product)
    }
    
    @Operation(
        summary = "更新理财产品",
        description = "更新理财产品基本信息，只有下架状态的产品才能修改"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "更新成功", 
        content = [Content(schema = Schema(implementation = FinancialProduct::class))]
    )
    @PostMapping("/update")
    fun update(@RequestBody request: FinancialProductUpdateRequest): R<FinancialProduct> {
        val product = financialProductService.updateProduct(request)
        return R.success(product)
    }
    
    @Operation(
        summary = "上架理财产品",
        description = "将理财产品状态改为上架"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "上架成功", 
        content = [Content(schema = Schema(implementation = FinancialProduct::class))]
    )
    @PostMapping("/online/{id}")
    fun online(
        @Parameter(description = "产品ID", required = true) @PathVariable id: Long,
        @Parameter(description = "上架备注") @RequestParam(required = false) remark: String?
    ): R<FinancialProduct> {
        val product = financialProductService.onlineProduct(id, remark)
        return R.success(product)
    }
    
    @Operation(
        summary = "下架理财产品",
        description = "将理财产品状态改为下架，可选择是否强制赎回该产品下所有生效中的订单"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "下架成功", 
        content = [Content(schema = Schema(implementation = FinancialProduct::class))]
    )
    @PostMapping("/offline")
    fun offline(@RequestBody request: FinancialProductOfflineRequest): R<FinancialProduct> {
        val product = financialProductService.offlineProduct(request)
        return R.success(product)
    }
    
    @Operation(
        summary = "获取理财产品详情",
        description = "根据产品ID获取理财产品详情"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "查询成功", 
        content = [Content(schema = Schema(implementation = FinancialProduct::class))]
    )
    @GetMapping("/info/{id}")
    fun getInfo(@Parameter(description = "产品ID", required = true) @PathVariable id: Long): R<FinancialProduct> {
        val product = financialProductService.getById(id)
            ?: return R.error("产品不存在")
        return R.success(product)
    }
}