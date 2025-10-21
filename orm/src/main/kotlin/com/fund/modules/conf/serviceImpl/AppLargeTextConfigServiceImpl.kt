package com.fund.modules.conf.serviceImpl;

import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.fund.modules.conf.model.AppLargeTextConfig;
import com.fund.modules.conf.mapper.AppLargeTextConfigMapper;
import com.fund.modules.conf.service.AppLargeTextConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.modules.conf.dto.EmailTemplate
import com.fund.modules.conf.dto.EmailTemplateConfig
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service;
import kotlin.code

/**
 * <p>
 * app配置(大文本) 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@Service
open class AppLargeTextConfigServiceImpl(
    private val redisTemplate: RedisTemplate<String, String>
) : ServiceImpl<AppLargeTextConfigMapper, AppLargeTextConfig>(), AppLargeTextConfigService {

    companion object {
        const val emailTemplateConfig = "emailTemplateConfig"
    }

    override fun getEmailTemplateConfig(): EmailTemplateConfig {
        val cache = redisTemplate.opsForValue().get(RedisKeys.App_EMAIL_TEMPLATE_CONFIG)
        if (StrUtil.isBlank(cache)) {
            val dbValue = getOne(
                KtQueryWrapper(AppLargeTextConfig())
                    .eq(AppLargeTextConfig::code, emailTemplateConfig)
                    .last("limit 1")
            )
            if (dbValue == null) {
                val defaultConfig = buildDefaultEmailTemplateConfig()
                setEmailTemplateConfig(defaultConfig)
                return defaultConfig
            }
            val value = dbValue.value!!
            redisTemplate.opsForValue().set(RedisKeys.App_EMAIL_TEMPLATE_CONFIG, value)
            return JSON.parseObject(value, EmailTemplateConfig::class.java)
        }
        return JSON.parseObject(cache, EmailTemplateConfig::class.java)
    }

    override fun setEmailTemplateConfig(dto: EmailTemplateConfig) {
        val old = getOne(
            KtQueryWrapper(AppLargeTextConfig())
                .eq(AppLargeTextConfig::code, emailTemplateConfig)
                .last("limit 1")
        )
        val value = JSON.toJSONString(dto)
        if (old == null) {
            val config = AppLargeTextConfig()
            config.code = emailTemplateConfig
            config.value = value
            redisTemplate.delete(RedisKeys.App_EMAIL_TEMPLATE_CONFIG)
            save(config)
            redisTemplate.delete(RedisKeys.App_EMAIL_TEMPLATE_CONFIG)
        } else {
            redisTemplate.delete(RedisKeys.App_EMAIL_TEMPLATE_CONFIG)
            update(
                KtUpdateWrapper(AppLargeTextConfig())
                    .eq(AppLargeTextConfig::code, emailTemplateConfig)
                    .set(AppLargeTextConfig::value, value)
            )
            redisTemplate.delete(RedisKeys.App_EMAIL_TEMPLATE_CONFIG)
        }
    }

    private fun buildDefaultEmailTemplateConfig(): EmailTemplateConfig {
        val cfg = EmailTemplateConfig()
        val register = EmailTemplate()
        register.enable = true
        register.subject = "注册验证码"
        register.htmlBody = "您的验证码为{}"

        val cashOutReview = EmailTemplate()
        cashOutReview.enable = true
        cashOutReview.subject = "提现审核通知"
        cashOutReview.htmlBody = "您的提现请求已通过,金额{}已到账"

        val cashAdd = EmailTemplate()
        cashAdd.enable = true
        cashAdd.subject = "余额变动通知"
        cashAdd.htmlBody = "您的余额已增加{}"

        val cashSub = EmailTemplate()
        cashSub.enable = true
        cashSub.subject = "余额变动通知"
        cashSub.htmlBody = "您的余额已减少{}"

        cfg.register = register
        cfg.cashOutReview = cashOutReview
        cfg.cashAdd = cashAdd
        cfg.cashSub = cashSub
        return cfg
    }

}
