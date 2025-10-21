package com.fund.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.fund.common.entity.R
import com.fund.modules.conf.dto.EmailTemplateConfig
import com.fund.modules.conf.dto.GmailConfig
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.conf.service.AppLargeTextConfigService
import com.fund.modules.sys.service.SysOptLogService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


/**
 * mange-系统配置
 */
@RestController
@RequestMapping(value = ["/manage/config"])
class AppConfigController(
    private val configService: AppConfigService,
    private val optLogService: SysOptLogService,
    private val largeTextConfigService: AppLargeTextConfigService
) {

    /**
     * 获取gmail配置
     */
    @PostMapping("/getGmailConfig")
    fun selectGmailConfig(): R<GmailConfig> {
        return R.success(configService.getConfig(GmailConfig::class.java))
    }

    /**
     * 设置gmail配置
     */
    @PostMapping("/setGmailConfig")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun changeGmailConfig(@RequestBody @Validated dto: GmailConfig): R<Unit> {
        configService.setConfig(dto)
        return R.success()
    }

    /**
     * 获取邮件模板配置
     */
    @GetMapping("/getEmailTemplateConfig")
    fun getEmailTemplateConfig(): R<EmailTemplateConfig> {
        return R.success(largeTextConfigService.getEmailTemplateConfig())
    }

    /**
     * 设置邮件模板配置
     */
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

}
