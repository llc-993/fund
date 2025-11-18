package com.fund.controller.sys

import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.modules.sys.menu.QueryOptLogReq
import com.fund.modules.sys.model.SysOptLog
import com.fund.modules.sys.service.SysOptLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "系统操作日志", description = "系统操作日志查询接口，用于查看和管理系统的操作记录")
@RestController
@RequestMapping(value = ["/optlog"])
class SysOptLogController(
    private val optLogService: SysOptLogService
) {

    @GetMapping("/page")
    @Operation(
        summary = "查询操作日志列表",
        description = "分页查询系统操作日志，支持按备注信息模糊搜索，按创建时间降序排列"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    fun page(
        @Parameter(description = "查询参数，支持按备注模糊搜索")
        req: QueryOptLogReq
    ) : R<Page<SysOptLog>> {
        val p = Page<SysOptLog>(req.pageNum, req.pageSize)
        val page = optLogService.page(
            p,
            KtQueryWrapper(SysOptLog())
                .like(StrUtil.isNotBlank(req.remark), SysOptLog::remark, req.remark)
                .orderByDesc(SysOptLog::createTime)
        )
        return R.success(page)
    }

}
