package com.fund.controller.payplatform

import cn.dev33.satoken.stp.StpUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.IdReq
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.modules.platform.model.AppPayPlatform
import com.fund.modules.platform.service.AppPayPlatformService
import com.fund.modules.sys.service.SysOptLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Tag(name = "manage-支付平台")
@RestController
@RequestMapping("/payplatform")
class PayPlatformController(
    private val platformService: AppPayPlatformService,
    private val sysOptLogService: SysOptLogService
) {

    @GetMapping("/page")
    @Operation(summary ="支付平台列表")
    fun page(req: PageReq): R<Page<AppPayPlatform>> {
        val p = Page<AppPayPlatform>(req.pageNum, req.pageSize)
        val page = platformService.page(p,
            KtQueryWrapper(AppPayPlatform())
                .orderByDesc(AppPayPlatform::sortBy, AppPayPlatform::id, AppPayPlatform::createTime)
        )
        return R.success(page)
    }

    @PostMapping("/add")
    @Operation(summary ="添加支付平台")
    fun add(@RequestBody @Validated req: AppPayPlatform): R<Unit> {
        platformService.save(req)
        sysOptLogService.addLog(StpUtil.getLoginIdAsLong(), "添加支付平台", JSON.toJSONString(req))
        return R.success()
    }

    @PostMapping("/update")
    @Operation(summary ="修改支付平台")
    fun update(@RequestBody @Validated req: AppPayPlatform): R<Unit> {
        platformService.updateById(req)
        sysOptLogService.addLog(StpUtil.getLoginIdAsLong(), "修改支付平台", JSON.toJSONString(req))
        return R.success()
    }

    @PostMapping("/del")
    @Operation(summary ="删除支付平台")
    fun del(@RequestBody @Validated req: IdReq): R<Unit> {
        platformService.removeById(req.id)
        sysOptLogService.addLog(StpUtil.getLoginIdAsLong(), "删除支付平台", JSON.toJSONString(req))
        return R.success()
    }




}