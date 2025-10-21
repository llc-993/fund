package com.fund.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.IdReq
import com.fund.common.entity.R
import com.fund.modules.sys.model.SysCsLink
import com.fund.modules.sys.service.SysCsLinkService
import com.fund.modules.sys.service.SysOptLogService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * mange-客服链接配置
 */
@RestController
@RequestMapping(value = ["/manage/csLink"])
class SysCsLinkController(
    private val sysCsLinkService: SysCsLinkService,
    private val optLogService: SysOptLogService,
) {

    /**
     * 客服链接列表
     */
    @GetMapping("/list")
    fun list(): R<List<SysCsLink>> {
        return R.success(sysCsLinkService.list(
            KtQueryWrapper(SysCsLink())
                .orderByDesc(SysCsLink::createTime)
        ))
    }

    /**
     * 添加客服链接
     */
    @PostMapping("/add")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun add(@RequestBody @Validated sysCsLink: SysCsLink): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysCsLinkService.save(sysCsLink)
        optLogService.addLog(adminId, "添加客服链接", JSON.toJSONString(sysCsLink))
        return R.success()
    }

    /**
     * 修改客服链接
     */
    @PostMapping("/update")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun update(@RequestBody @Validated sysCsLink: SysCsLink): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysCsLinkService.updateById(sysCsLink)
        optLogService.addLog(adminId, "修改客服链接", JSON.toJSONString(sysCsLink))
        return R.success()
    }

    /**
     * 删除客服链接
     */
    @PostMapping("/del")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun del(@RequestBody @Validated req: IdReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysCsLinkService.removeById(req.id)
        optLogService.addLog(adminId, "删除客服链接", JSON.toJSONString(req))
        return R.success()
    }

}