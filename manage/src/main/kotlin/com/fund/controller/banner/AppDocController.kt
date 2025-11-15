package com.fund.controller.banner

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.doc.model.AppDoc
import com.fund.modules.doc.service.AppDocService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@Tag(name = "App 文案管理", description = "后台 App 文案（AppDoc）配置的查询、新增、编辑、删除接口")
@RestController
@RequestMapping("/doc")
class AppDocController(
    private val appDocService: AppDocService
) {

    @Operation(
        summary = "查询文案列表",
        description = "按照用途、国际化标识筛选 App 文案列表，默认按 sort、创建时间倒序"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SaCheckLogin
    @GetMapping("/list")
    fun list(
        @Parameter(description = "文案用途标识", example = "home_popup")
        @RequestParam(value = "usedFor", required = false) usedFor: String?,
        @Parameter(description = "国际化标识编码", example = "doc_register_tip")
        @RequestParam(value = "i18nCode", required = false) i18nCode: String?
    ): R<List<AppDoc>> {
        val wrapper = KtQueryWrapper(AppDoc())
            .eq(!usedFor.isNullOrBlank(), AppDoc::usedFor, usedFor)
            .eq(!i18nCode.isNullOrBlank(), AppDoc::i18nCode, i18nCode)
            .orderByDesc(AppDoc::sortBy)
            .orderByDesc(AppDoc::createTime)
        return R.success(appDocService.list(wrapper))
    }

    @Operation(summary = "获取文案详情", description = "根据文案ID查询详细内容")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SaCheckLogin
    @GetMapping("/{id}")
    fun detail(
        @Parameter(description = "文案ID", required = true, example = "1")
        @PathVariable id: Long
    ): R<AppDoc> {
        val doc = appDocService.getById(id) ?: throw BusinessException("app_doc_not_found")
        return R.success(doc)
    }

    @Operation(summary = "新增文案", description = "创建 App 文案，支持配置标题、内容、国际化标识等信息")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @SaCheckLogin
    @PostMapping
    fun create(@RequestBody @Validated doc: AppDoc): R<AppDoc> {
        val operator = currentOperator()
        doc.id = null
        doc.createBy = operator
        doc.updateBy = operator
        doc.createTime = LocalDateTime.now()
        doc.updateTime = LocalDateTime.now()

        if (!appDocService.save(doc)) {
            throw BusinessException("app_doc_create_failed")
        }
        return R.success(doc)
    }

    @Operation(summary = "更新文案", description = "根据 ID 更新 App 文案信息")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SaCheckLogin
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "文案ID", required = true, example = "1")
        @PathVariable id: Long,
        @RequestBody @Validated doc: AppDoc
    ): R<AppDoc> {
        val existing = appDocService.getById(id) ?: throw BusinessException("app_doc_not_found")
        val operator = currentOperator()

        doc.id = existing.id
        doc.createBy = existing.createBy
        doc.createTime = existing.createTime
        doc.updateBy = operator
        doc.updateTime = LocalDateTime.now()

        if (!appDocService.updateById(doc)) {
            throw BusinessException("app_doc_update_failed")
        }
        return R.success(doc)
    }

    @Operation(summary = "删除文案", description = "根据 ID 删除 App 文案")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    fun delete(
        @Parameter(description = "文案ID", required = true, example = "1")
        @PathVariable id: Long
    ): R<Unit> {
        if (!appDocService.removeById(id)) {
            throw BusinessException("app_doc_not_found")
        }
        return R.success()
    }

    private fun currentOperator(): String {
        return runCatching { StpUtil.getLoginIdAsString() }.getOrDefault("system")
    }
}