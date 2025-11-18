package com.fund.controller.stock

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.service.StockService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema

@RestController
@RequestMapping("/stock")
@Tag(name = "股票管理", description = "管理员查询股票数据")
class StockController (
    private val stockService: StockService
){

    @Operation(
        summary = "股票列表",
        description = "分页查询股票列表"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = Stock::class))])
    @GetMapping("/page")
    fun list(req: PageReq) :R<Any> {
        val page = Page<Stock>(req.pageNum, req.pageSize)
        val page1 = stockService.page(
            page, KtQueryWrapper(Stock())
                .orderByDesc(Stock::symbol)
        )
        return R.success(page1)
    }



}