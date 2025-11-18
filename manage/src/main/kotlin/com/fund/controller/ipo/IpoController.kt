package com.fund.controller.ipo

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.IdReq
import com.fund.common.entity.R
import com.fund.modules.ipo.AdminIpoAddRequest
import com.fund.modules.ipo.AdminIpoQueryRequest
import com.fund.modules.ipo.AdminIpoUpdateRequest
import com.fund.modules.ipo.model.Ipo
import com.fund.modules.ipo.model.StockSubscription
import com.fund.modules.ipo.service.IpoService
import com.fund.modules.ipo.service.StockSubscriptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.BeanUtils
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema

@Tag(name = "IPO管理", description = "IPO列表查询、新增、修改、删除等接口")
@RestController
@RequestMapping("/ipo")
class IpoController(
    private val ipoService: IpoService,
    private val stockSubscriptionService: StockSubscriptionService
) {

    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "分页查询IPO列表",
        description = "分页查询IPO列表，支持按股票代码和名称筛选"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = Ipo::class))])
    @GetMapping("list")
    fun list( req: AdminIpoQueryRequest): R<Any> {
        val page: Page<Ipo> = Page(req.pageNum, req.pageSize)

        val page1 = ipoService.page(
            page, KtQueryWrapper(Ipo())
                .eq(StringUtils.isNotBlank(req.symbol), Ipo::symbol, req.symbol)
                .eq(StringUtils.isNotBlank(req.name), Ipo::name, req.name)
                .orderByDesc(Ipo::id)
        )

        return R.success(page1)
    }

    @Operation(
        summary = "新增IPO",
        description = "新增IPO信息，需要提供股票代码、名称、价格等信息"
    )
    @ApiResponse(responseCode = "200", description = "新增成功")
    @PostMapping("add")
    fun add(@RequestBody req: AdminIpoAddRequest): R<Any> {
        try {
            // 参数校验
            if (StringUtils.isBlank(req.name)) {
                return R.error("IPO名称不能为空")
            }
            if (StringUtils.isBlank(req.symbol)) {
                return R.error("股票代码不能为空")
            }
            if (req.price == null) {
                return R.error("价格不能为空")
            }

            // 检查股票代码是否已存在
            val existingIpo = ipoService.getOne(
                KtQueryWrapper(Ipo())
                    .eq(Ipo::symbol, req.symbol)
            )
            if (existingIpo != null) {
                return R.error("股票代码已存在")
            }

            // 创建IPO对象
            val ipo = Ipo()
            BeanUtils.copyProperties(req, ipo)

            // 保存到数据库
            val success = ipoService.save(ipo)
            if (!success) {
                return R.error("新增IPO失败")
            }

            logger.info("新增IPO成功: symbol=${req.symbol}, name=${req.name}")
            return R.success()
        } catch (e: Exception) {
            logger.error(e) { "新增IPO异常" }
            return R.error("新增IPO失败")
        }
    }

    @Operation(
        summary = "修改IPO",
        description = "修改IPO信息，需要提供IPO ID和要修改的字段"
    )
    @ApiResponse(responseCode = "200", description = "修改成功")
    @PostMapping("update")
    fun update(@RequestBody req: AdminIpoUpdateRequest): R<Any> {
        try {
            // 参数校验
            if (req.id == null) {
                return R.error("IPO ID不能为空")
            }

            // 检查IPO是否存在
            val existingIpo = ipoService.getById(req.id)
                ?: return R.error("IPO不存在")

            // 如果修改了股票代码，检查新代码是否已被使用
            if (StringUtils.isNotBlank(req.symbol) && req.symbol != existingIpo.symbol) {
                val duplicateIpo = ipoService.getOne(
                    KtQueryWrapper(Ipo())
                        .eq(Ipo::symbol, req.symbol)
                        .ne(Ipo::id, req.id)
                )
                if (duplicateIpo != null) {
                    return R.error("股票代码已存在")
                }
            }

            // 更新IPO对象
            BeanUtils.copyProperties(req, existingIpo, "id", "createTime")

            // 保存到数据库
            val success = ipoService.updateById(existingIpo)
            if (!success) {
                return R.error("修改IPO失败")
            }

            logger.info("修改IPO成功: id=${req.id}, symbol=${req.symbol}")
            return R.success()

        } catch (e: Exception) {
            logger.error(e) { "修改IPO异常" }
            return R.error("修改IPO失败")
        }
    }

    @Operation(
        summary = "删除IPO",
        description = "删除IPO，已有用户认购的IPO不能删除"
    )
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PostMapping("delete")
    fun delete(@RequestBody req: IdReq): R<Any> {
        try {
            // 参数校验
            if (req.id == null) {
                return R.error("IPO ID不能为空")
            }

            // 检查IPO是否存在
            val existingIpo = ipoService.getById(req.id)
                ?: return R.error("IPO不存在")

            // 检查是否可以删除（例如：已有用户认购的IPO不能删除）
            val l = stockSubscriptionService.count(
                KtQueryWrapper(StockSubscription())
                    .eq(StockSubscription::ipoId, req.id)
            )

            if (l > 0) {
                return R.error("已有用户认购的IPO不能删除")
            }

            // 删除IPO
            val success = ipoService.removeById(req.id)
            if (!success) {
                return R.error("删除IPO失败")
            }

            return R.success()
        } catch (e: Exception) {
            logger.error(e) { "删除IPO异常" }
            return R.error("删除IPO失败")
        }
    }

    @Operation(
        summary = "查询IPO详情",
        description = "根据ID查询IPO详细信息"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("detail/{id}")
    fun detail(@PathVariable id: Long): R<Any> {
        try {
            val ipo = ipoService.getById(id)
                ?: return R.error("IPO不存在")

            return R.success(ipo)

        } catch (e: Exception) {
            logger.error(e) { "查询IPO详情异常" }
            return R.error("查询IPO详情失败")
        }
    }

}