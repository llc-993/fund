package com.fund.controller.banner

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.banner.model.AppBanner
import com.fund.modules.banner.service.AppBannerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Banner管理", description = "后台 Banner 轮播图的查询、新增、编辑、删除接口")
@RestController
@RequestMapping("/banner")
class BannerController(
    private val appBannerService: AppBannerService
) {

    @Operation(
        summary = "查询 Banner 列表",
        description = "按照状态、国际化标识筛选所有 Banner，默认按 sort 值和创建时间倒序"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SaCheckLogin
    @GetMapping("/list")
    fun list(
        @Parameter(description = "状态（0=关闭，1=开启）", example = "1")
        @RequestParam(value = "status", required = false) status: Boolean?,
        @Parameter(description = "国际化标识编码", example = "banner_home_top")
        @RequestParam(value = "i18nCode", required = false) i18nCode: String?
    ): R<List<AppBanner>> {
        val wrapper = KtQueryWrapper(AppBanner())
            .eq(status != null, AppBanner::bannerStatus, status)
            .eq(!i18nCode.isNullOrBlank(), AppBanner::i18nCode, i18nCode)
            .orderByDesc(AppBanner::sortBy)
            .orderByDesc(AppBanner::createTime)
        return R.success(appBannerService.list(wrapper))
    }

    @Operation(summary = "获取 Banner 详情", description = "根据 Banner ID 查询详细信息")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SaCheckLogin
    @GetMapping("/{id}")
    fun detail(
        @Parameter(description = "Banner 主键ID", required = true, example = "1")
        @PathVariable id: Long
    ): R<AppBanner> {
        val banner = appBannerService.getById(id) ?: throw BusinessException("banner_not_found")
        return R.success(banner)
    }

    @Operation(summary = "新增 Banner", description = "创建新的 Banner 轮播图配置")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @SaCheckLogin
    @PostMapping("/save")
    fun create(@RequestBody @Validated banner: AppBanner): R<AppBanner> {
        val operator = currentOperator()
        banner.id = null
        banner.createBy = operator
        banner.updateBy = operator
        banner.createTime = LocalDateTime.now()
        banner.updateTime = LocalDateTime.now()

        if (!appBannerService.save(banner)) {
            throw BusinessException("banner_create_failed")
        }
        return R.success(banner)
    }

    @Operation(summary = "更新 Banner", description = "根据 ID 更新 Banner 信息")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SaCheckLogin
    @PostMapping("/update")
    fun update(
        @Parameter(description = "Banner 主键ID", required = true, example = "1")
        @PathVariable id: Long,
        @RequestBody @Validated banner: AppBanner
    ): R<AppBanner> {
        val existing = appBannerService.getById(id) ?: throw BusinessException("banner_not_found")
        val operator = currentOperator()

        banner.id = existing.id
        banner.createBy = existing.createBy
        banner.createTime = existing.createTime
        banner.updateBy = operator
        banner.updateTime = LocalDateTime.now()

        if (!appBannerService.updateById(banner)) {
            throw BusinessException("banner_update_failed")
        }
        return R.success(banner)
    }

    @Operation(summary = "删除 Banner", description = "根据 ID 删除 Banner")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SaCheckLogin
    @PostMapping("/del")
    fun delete(
        @Parameter(description = "Banner 主键ID", required = true, example = "1")
        @PathVariable id: Long
    ): R<Unit> {
        if (!appBannerService.removeById(id)) {
            throw BusinessException("banner_not_found")
        }
        return R.success()
    }

    private fun currentOperator(): String {
        return runCatching { StpUtil.getLoginIdAsString() }.getOrDefault("system")
    }
}