package com.fund.controller.banner

import cn.dev33.satoken.annotation.SaCheckLogin
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.notice.model.AppNotice
import com.fund.modules.notice.service.AppNoticeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@Tag(name = "App 公告管理", description = "后台 App 公告（AppNotice）配置的查询、新增、编辑、删除接口")
@RestController
@RequestMapping("/notice")
class AppNoticeController(
    private val appNoticeService: AppNoticeService
) {

    @Operation(summary = "公告列表", description = "按状态、类型、语言筛选公告，默认按创建时间和ID倒序")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SaCheckLogin
    @GetMapping("/list")
    fun list(
        @Parameter(description = "公告状态（0=关闭，1=启用）", example = "1")
        @RequestParam(value = "status", required = false) status: Byte?,
        @Parameter(description = "公告类型", example = "2")
        @RequestParam(value = "type", required = false) type: Byte?,
        @Parameter(description = "语言编码", example = "zh-tw")
        @RequestParam(value = "language", required = false) language: String?
    ): R<List<AppNotice>> {
        val wrapper = KtQueryWrapper(AppNotice())
            .eq(status != null, AppNotice::status, status)
            .eq(type != null, AppNotice::type, type)
            .eq(!language.isNullOrBlank(), AppNotice::language, language)
            .orderByDesc(AppNotice::createTime)
            .orderByDesc(AppNotice::id)
        return R.success(appNoticeService.list(wrapper))
    }

    @Operation(summary = "公告详情", description = "根据 ID 查询公告详情")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SaCheckLogin
    @GetMapping("/{id}")
    fun detail(
        @Parameter(description = "公告ID", required = true, example = "8")
        @PathVariable id: Long
    ): R<AppNotice> {
        val notice = appNoticeService.getById(id) ?: throw BusinessException("app_notice_not_found")
        return R.success(notice)
    }

    @Operation(summary = "新增公告", description = "创建新的 App 公告")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @SaCheckLogin
    @PostMapping
    fun create(@RequestBody @Validated notice: AppNotice): R<AppNotice> {
        notice.id = null
        notice.createTime = LocalDateTime.now()
        notice.updateTime = LocalDateTime.now()
        if (!appNoticeService.save(notice)) {
            throw BusinessException("app_notice_create_failed")
        }
        return R.success(notice)
    }

    @Operation(summary = "更新公告", description = "根据 ID 更新 App 公告内容")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SaCheckLogin
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "公告ID", required = true, example = "8")
        @PathVariable id: Long,
        @RequestBody @Validated notice: AppNotice
    ): R<AppNotice> {
        val existing = appNoticeService.getById(id) ?: throw BusinessException("app_notice_not_found")
        notice.id = existing.id
        notice.createTime = existing.createTime
        notice.updateTime = LocalDateTime.now()
        if (!appNoticeService.updateById(notice)) {
            throw BusinessException("app_notice_update_failed")
        }
        return R.success(notice)
    }

    @Operation(summary = "删除公告", description = "根据 ID 删除 App 公告")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    fun delete(
        @Parameter(description = "公告ID", required = true, example = "8")
        @PathVariable id: Long
    ): R<Unit> {
        if (!appNoticeService.removeById(id)) {
            throw BusinessException("app_notice_not_found")
        }
        return R.success()
    }
}