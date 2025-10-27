package com.fund.controller.block

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.IdReq
import com.fund.common.entity.R
import com.fund.modules.block.AdminBlockTradeAddRequest
import com.fund.modules.block.AdminBlockTradeQueryRequest
import com.fund.modules.block.AdminBlockTradeUpdateRequest
import com.fund.modules.block.model.StockBlockTrade
import com.fund.modules.block.model.StockBlockTradeSubscription
import com.fund.modules.block.service.StockBlockTradeService
import com.fund.modules.block.service.StockBlockTradeSubscriptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.BeanUtils
import org.springframework.web.bind.annotation.*

@Tag(name = "大宗交易管理", description = "大宗交易列表查询、新增、修改、删除等接口")
@RestController
@RequestMapping("/block")
class StockBlockTradeController(
    private val stockBlockTradeService: StockBlockTradeService,
    private val stockBlockTradeSubscriptionService: StockBlockTradeSubscriptionService
) {

    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "分页查询大宗交易列表",
        description = "分页查询大宗交易列表，支持按名称和状态筛选"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("list")
    fun list(@RequestBody req: AdminBlockTradeQueryRequest): R<Any> {
        val page: Page<StockBlockTrade> = Page(req.pageNum, req.pageSize)

        val page1 = stockBlockTradeService.page(
            page, KtQueryWrapper(StockBlockTrade())
                .eq(StringUtils.isNotBlank(req.name), StockBlockTrade::name, req.name)
                .eq(req.status != null, StockBlockTrade::status, req.status)
                .orderByDesc(StockBlockTrade::id)
        )

        return R.success(page1)
    }

    @Operation(
        summary = "新增大宗交易",
        description = "新增大宗交易信息，需要提供股票名称、股票ID、折扣等信息"
    )
    @ApiResponse(responseCode = "200", description = "新增成功")
    @PostMapping("add")
    fun add(@RequestBody req: AdminBlockTradeAddRequest): R<Any> {
        try {
            // 参数校验
            if (StringUtils.isBlank(req.name)) {
                return R.error("股票名称不能为空")
            }
            if (req.stockId == null) {
                return R.error("股票ID不能为空")
            }
            if (req.discount == null) {
                return R.error("折扣不能为空")
            }

            // 创建大宗交易对象
            val blockTrade = StockBlockTrade()
            BeanUtils.copyProperties(req, blockTrade)

            // 保存到数据库
            val success = stockBlockTradeService.save(blockTrade)
            if (!success) {
                return R.error("新增大宗交易失败")
            }

            logger.info("新增大宗交易成功: name=${req.name}, stockId=${req.stockId}")
            return R.success(blockTrade)
        } catch (e: Exception) {
            logger.error(e) { "新增大宗交易异常" }
            return R.error("新增大宗交易失败")
        }
    }

    @Operation(
        summary = "修改大宗交易",
        description = "修改大宗交易信息，需要提供大宗交易ID和要修改的字段"
    )
    @ApiResponse(responseCode = "200", description = "修改成功")
    @PostMapping("update")
    fun update(@RequestBody req: AdminBlockTradeUpdateRequest): R<Any> {
        try {
            // 参数校验
            if (req.id == null) {
                return R.error("大宗交易ID不能为空")
            }

            // 检查大宗交易是否存在
            val existingBlockTrade = stockBlockTradeService.getById(req.id)
                ?: return R.error("大宗交易不存在")

            // 更新大宗交易对象
            BeanUtils.copyProperties(req, existingBlockTrade, "id")

            // 保存到数据库
            val success = stockBlockTradeService.updateById(existingBlockTrade)
            if (!success) {
                return R.error("修改大宗交易失败")
            }

            logger.info("修改大宗交易成功: id=${req.id}, name=${req.name}")
            return R.success(existingBlockTrade)

        } catch (e: Exception) {
            logger.error(e) { "修改大宗交易异常" }
            return R.error("修改大宗交易失败")
        }
    }

    @Operation(
        summary = "删除大宗交易",
        description = "删除大宗交易，已有用户申购的大宗交易不能删除"
    )
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PostMapping("delete")
    fun delete(@RequestBody req: IdReq): R<Any> {
        try {
            // 参数校验
            if (req.id == null) {
                return R.error("大宗交易ID不能为空")
            }

            // 检查大宗交易是否存在
            val existingBlockTrade = stockBlockTradeService.getById(req.id)
                ?: return R.error("大宗交易不存在")

            // 检查是否可以删除（例如：已有用户申购的大宗交易不能删除）
            val count = stockBlockTradeSubscriptionService.count(
                KtQueryWrapper(StockBlockTradeSubscription())
                    .eq(StockBlockTradeSubscription::blockTradeId, req.id)
            )

            if (count > 0) {
                return R.error("已有用户申购的大宗交易不能删除")
            }

            // 删除大宗交易
            val success = stockBlockTradeService.removeById(req.id)
            if (!success) {
                return R.error("删除大宗交易失败")
            }

            logger.info("删除大宗交易成功: id=${req.id}")
            return R.success()
        } catch (e: Exception) {
            logger.error(e) { "删除大宗交易异常" }
            return R.error("删除大宗交易失败")
        }
    }

    @Operation(
        summary = "查询大宗交易详情",
        description = "根据ID查询大宗交易详细信息"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("detail/{id}")
    fun detail(@PathVariable id: Int): R<Any> {
        try {
            val blockTrade = stockBlockTradeService.getById(id)
                ?: return R.error("大宗交易不存在")

            return R.success(blockTrade)

        } catch (e: Exception) {
            logger.error(e) { "查询大宗交易详情异常" }
            return R.error("查询大宗交易详情失败")
        }
    }

}

