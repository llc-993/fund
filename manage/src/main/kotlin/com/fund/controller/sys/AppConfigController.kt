package com.fund.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.fund.common.entity.R
import com.fund.modules.conf.dto.BaseConfig
import com.fund.modules.conf.dto.EmailTemplateConfig
import com.fund.modules.conf.dto.GmailConfig
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.conf.service.AppLargeTextConfigService
import com.fund.modules.sys.service.SysOptLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@Tag(name = "系统配置", description = "系统配置相关接口，包括Gmail配置、邮件模板配置等")
@RestController
@RequestMapping(value = ["/config"])
class AppConfigController(
    private val configService: AppConfigService,
    private val optLogService: SysOptLogService,
    private val largeTextConfigService: AppLargeTextConfigService
) {

    @Operation(
        summary = "获取Gmail配置",
        description = "获取系统Gmail邮箱配置"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @PostMapping("/getGmailConfig")
    fun selectGmailConfig(): R<GmailConfig> {
        return R.success(configService.getConfig(GmailConfig::class.java))
    }

    @Operation(
        summary = "设置Gmail配置",
        description = "设置系统Gmail邮箱配置，需要管理员权限"
    )
    @ApiResponse(responseCode = "200", description = "设置成功")
    @PostMapping("/setGmailConfig")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun changeGmailConfig(@RequestBody @Validated dto: GmailConfig): R<Unit> {
        configService.setConfig(dto)
        return R.success()
    }

    @Operation(
        summary = "获取邮件模板配置",
        description = "获取系统邮件模板配置"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/getEmailTemplateConfig")
    fun getEmailTemplateConfig(): R<EmailTemplateConfig> {
        return R.success(largeTextConfigService.getEmailTemplateConfig())
    }

    @Operation(
        summary = "设置邮件模板配置",
        description = "设置系统邮件模板配置，需要管理员权限"
    )
    @ApiResponse(responseCode = "200", description = "设置成功")
    @PostMapping("/setEmailTemplateConfig")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun setEmailTemplateConfig(@RequestBody @Validated dto: EmailTemplateConfig): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        largeTextConfigService.setEmailTemplateConfig(dto)
        optLogService.addLog(adminId, "设置邮件模板配置", JSON.toJSONString(dto))
        return R.success()
    }

    @PostMapping("/getBaseConfig")
    @Operation(summary = "获取基础配置")
    fun selectBaseConfig(): R<BaseConfig> {
        return R.success(configService.getConfig(BaseConfig::class.java))
    }

    @PostMapping("/setBaseConfig")
    @Operation(summary = "设置基础配置")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun changeBaseConfig(@RequestBody @Validated dto: BaseConfig): R<Unit> {
        configService.setConfig(dto)
        val adminId = StpUtil.getLoginIdAsLong()
        optLogService.addLog(adminId, "设置基础配置", JSON.toJSONString(dto))
        return R.success()
    }

}
