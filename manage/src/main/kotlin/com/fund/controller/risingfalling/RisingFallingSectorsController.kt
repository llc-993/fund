package com.fund.controller.risingFalling

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.IdReq
import com.fund.common.entity.R
import com.fund.modules.risingFalling.AdminRisingFallingSectorsAddRequest
import com.fund.modules.risingFalling.AdminRisingFallingSectorsQueryRequest
import com.fund.modules.risingFalling.AdminRisingFallingSectorsUpdateRequest
import com.fund.modules.risingFalling.model.RisingFallingSectors
import com.fund.modules.risingFalling.model.RisingFallingSectorsSubscription
import com.fund.modules.risingFalling.service.RisingFallingSectorsService
import com.fund.modules.risingFalling.service.RisingFallingSectorsSubscriptionService
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.BeanUtils
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/risingFallingSectors")
class RisingFallingSectorsController(
    private val risingFallingSectorsService: RisingFallingSectorsService,
    private val risingFallingSectorsSubscriptionService: RisingFallingSectorsSubscriptionService
) {

    private val logger = KotlinLogging.logger {}

    /**
     * 分页查询涨跌板块列表
     */
    @GetMapping("list")
    fun list(@RequestBody req: AdminRisingFallingSectorsQueryRequest): R<Any> {
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

    /**
     * 新增涨跌板块
     */
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

    /**
     * 修改涨跌板块
     */
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

    /**
     * 删除涨跌板块
     */
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

    /**
     * 根据ID查询涨跌板块详情
     */
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
