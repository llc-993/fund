package com.fund.controller.risingFalling

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.IdReq
import com.fund.common.entity.R
import com.fund.modules.ipo.model.Ipo
import com.fund.modules.risingfalling.AdminRisingFallingSectorsAddRequest
import com.fund.modules.risingfalling.AdminRisingFallingSectorsQueryRequest
import com.fund.modules.risingfalling.AdminRisingFallingSectorsUpdateRequest
import com.fund.modules.risingFalling.model.RisingFallingSectors
import com.fund.modules.risingFalling.model.RisingFallingSectorsSubscription
import com.fund.modules.risingFalling.service.RisingFallingSectorsService
import com.fund.modules.risingFalling.service.RisingFallingSectorsSubscriptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.BeanUtils
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Tag(name = "涨跌板块管理", description = "涨跌板块列表查询、新增、修改、删除等接口")
@RestController
@RequestMapping("/risingFallingSectors")
class RisingFallingSectorsController(
    private val risingFallingSectorsService: RisingFallingSectorsService,
    private val risingFallingSectorsSubscriptionService: RisingFallingSectorsSubscriptionService
) {

    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "分页查询涨跌板块列表",
        description = "分页查询涨跌板块列表，支持按股票代码、锁定状态、显示状态筛选"
    )
    @ApiResponse(description = "查询成功",
        content = [Content(schema = Schema(implementation = RisingFallingSectors::class))])
    @GetMapping("list")
    fun list( req: AdminRisingFallingSectorsQueryRequest): R<Any> {
        val page: Page<RisingFallingSectors> = Page(req.pageNum, req.pageSize)

        val page1 = risingFallingSectorsService.page(
            page, KtQueryWrapper(RisingFallingSectors())
                .eq(StringUtils.isNotBlank(req.symbol), RisingFallingSectors::symbol, req.symbol)
                .eq(req.stockLockStatus != null, RisingFallingSectors::stockLockStatus, req.stockLockStatus)
                .eq(req.displayStatus != null, RisingFallingSectors::displayStatus, req.displayStatus)
                .orderByDesc(RisingFallingSectors::id)
        )

        return R.success(page1)
    }

    @Operation(
        summary = "新增涨跌板块",
        description = "新增涨跌板块信息，需要提供交易对、股票ID等信息"
    )
    @ApiResponse(responseCode = "200", description = "新增成功")
    @PostMapping("add")
    fun add(@RequestBody req: AdminRisingFallingSectorsAddRequest): R<Any> {
        try {
            // 参数校验
            if (StringUtils.isBlank(req.symbol)) {
                return R.error("交易对不能为空")
            }
            if (req.stockId == null) {
                return R.error("股票ID不能为空")
            }

            // 创建涨跌板块对象
            val risingFallingSectors = RisingFallingSectors()
            BeanUtils.copyProperties(req, risingFallingSectors)
            risingFallingSectors.createTime = LocalDateTime.now()

            // 保存到数据库
            val success = risingFallingSectorsService.save(risingFallingSectors)
            if (!success) {
                return R.error("新增涨跌板块失败")
            }

            logger.info("新增涨跌板块成功: symbol=${req.symbol}, stockId=${req.stockId}")
            return R.success(risingFallingSectors)
        } catch (e: Exception) {
            logger.error(e) { "新增涨跌板块异常" }
            return R.error("新增涨跌板块失败")
        }
    }

    @Operation(
        summary = "修改涨跌板块",
        description = "修改涨跌板块信息，需要提供涨跌板块ID和要修改的字段"
    )
    @ApiResponse(responseCode = "200", description = "修改成功")
    @PostMapping("update")
    fun update(@RequestBody req: AdminRisingFallingSectorsUpdateRequest): R<Any> {
        try {
            // 参数校验
            if (req.id == null) {
                return R.error("涨跌板块ID不能为空")
            }

            // 检查涨跌板块是否存在
            val existingRisingFallingSectors = risingFallingSectorsService.getById(req.id)
                ?: return R.error("涨跌板块不存在")

            // 更新涨跌板块对象
            BeanUtils.copyProperties(req, existingRisingFallingSectors, "id")

            // 保存到数据库
            val success = risingFallingSectorsService.updateById(existingRisingFallingSectors)
            if (!success) {
                return R.error("修改涨跌板块失败")
            }

            logger.info("修改涨跌板块成功: id=${req.id}, symbol=${req.symbol}")
            return R.success(existingRisingFallingSectors)

        } catch (e: Exception) {
            logger.error(e) { "修改涨跌板块异常" }
            return R.error("修改涨跌板块失败")
        }
    }

    @Operation(
        summary = "删除涨跌板块",
        description = "删除涨跌板块，已有用户申购的涨跌板块不能删除"
    )
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PostMapping("delete")
    fun delete(@RequestBody req: IdReq): R<Any> {
        try {
            // 参数校验
            if (req.id == null) {
                return R.error("涨跌板块ID不能为空")
            }

            // 检查涨跌板块是否存在
            val existingRisingFallingSectors = risingFallingSectorsService.getById(req.id)
                ?: return R.error("涨跌板块不存在")

            // 检查是否可以删除（例如：已有用户申购的涨跌板块不能删除）
            val count = risingFallingSectorsSubscriptionService.count(
                KtQueryWrapper(RisingFallingSectorsSubscription())
                    .eq(RisingFallingSectorsSubscription::risingFallingSectorsId, req.id)
            )

            if (count > 0) {
                return R.error("已有用户申购的涨跌板块不能删除")
            }

            // 删除涨跌板块
            val success = risingFallingSectorsService.removeById(req.id)
            if (!success) {
                return R.error("删除涨跌板块失败")
            }

            logger.info("删除涨跌板块成功: id=${req.id}")
            return R.success()
        } catch (e: Exception) {
            logger.error(e) { "删除涨跌板块异常" }
            return R.error("删除涨跌板块失败")
        }
    }

    @Operation(
        summary = "查询涨跌板块详情",
        description = "根据ID查询涨跌板块详细信息"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("detail/{id}")
    fun detail(@PathVariable id: Long): R<Any> {
        try {
            val risingFallingSectors = risingFallingSectorsService.getById(id)
                ?: return R.error("涨跌板块不存在")

            return R.success(risingFallingSectors)

        } catch (e: Exception) {
            logger.error(e) { "查询涨跌板块详情异常" }
            return R.error("查询涨跌板块详情失败")
        }
    }

}
