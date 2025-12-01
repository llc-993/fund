package com.fund.controller.news

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.modules.kline.Kline
import com.fund.modules.news.model.StockNews
import com.fund.modules.news.service.StockNewsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/news"])
@Tag(name = "新闻数据", description = "提供股票新闻数据接口")
class NewsController(
    private val newsService: StockNewsService
) {

    @Operation(
        summary = "新闻数据数据"
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = [Content(schema = Schema(implementation = StockNews::class))]
    )
    @GetMapping( "page")
    fun page (req: PageReq): R<Any> {
        val page:Page<StockNews> = Page(req.pageNum, req.pageSize)

        val page1 = newsService.page(
            page, KtQueryWrapper(StockNews::class.java)
                .orderByDesc(StockNews::id)
        )

        return R.success(page1)
    }

}