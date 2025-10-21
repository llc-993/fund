package com.fund.modules.conf.service;

import com.fund.modules.conf.model.AppLargeTextConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.modules.conf.dto.EmailTemplateConfig

/**
 * <p>
 * app配置(大文本) 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
interface AppLargeTextConfigService : IService<AppLargeTextConfig> {


    fun getEmailTemplateConfig(): EmailTemplateConfig

    fun setEmailTemplateConfig(dto: EmailTemplateConfig)
}
