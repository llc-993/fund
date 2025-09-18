package com.fund.modules.conf.service;

import com.fund.modules.conf.model.AppConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.modules.conf.enum.AppConfigCode

/**
 * <p>
 * app配置 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
interface AppConfigService : IService<AppConfig> {

    fun getValue(code: String): String?

    fun getValue(code: AppConfigCode): String?

    fun getValueOrDefault(code: AppConfigCode): String?

    fun getValueOrDefault(code: AppConfigCode, defaultValue: String): String?

    fun <T> getConfig(clazz: Class<T>): T

    fun <T> setConfig(dto: T)

    fun set(code:AppConfigCode, value: Any)

}
