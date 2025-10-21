package org.lemon.api.controller.sys

import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.lemon.api.common.domain.R
import org.lemon.api.modules.sys.domain.co.QueryOptLogReq
import org.lemon.api.modules.sys.domain.entity.SysOptLog
import org.lemon.api.modules.sys.service.SysOptLogService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["mange-系统日志接口"])
@RestController
@RequestMapping(value = ["/manage/optlog"])
class SysOptLogController(
    private val optLogService: SysOptLogService
) {

    @GetMapping("/page")
    @ApiOperation("日志列表")
    fun page(req: QueryOptLogReq) : R<Page<SysOptLog>> {
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
