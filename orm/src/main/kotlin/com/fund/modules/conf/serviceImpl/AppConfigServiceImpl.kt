package com.fund.modules.conf.serviceImpl;

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.fund.modules.conf.model.AppConfig;
import com.fund.modules.conf.mapper.AppConfigMapper;
import com.fund.modules.conf.service.AppConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.modules.conf.enum.AppConfigCode
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service;
import java.io.Serializable
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.code
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

/**
 * <p>
 * app配置 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@Service
open class AppConfigServiceImpl(
    // 从构造器中注入 redisTemplate
    private val redisTemplate: RedisTemplate<String, Any>
)
    : ServiceImpl<AppConfigMapper, AppConfig>(), AppConfigService {

    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun init() {
        redisTemplate.delete(RedisKeys.APPCONFIG)
    }

    override fun getValue(code: String): String? {
        var value: String? = redisTemplate.opsForHash<String, String>().get(RedisKeys.APPCONFIG, code)
        if (StrUtil.isBlank(value)) {
            value = null
        }
        value?.let {
            return it
        }
        val appConfig: AppConfig? = baseMapper.selectOne(
            KtQueryWrapper(AppConfig())
                .eq(AppConfig::code, code)
                .last("limit 1")
        )
        appConfig?.value?.let {
            redisTemplate.opsForHash<String, String>().put(RedisKeys.APPCONFIG, code, it)
            redisTemplate.expire(RedisKeys.APPCONFIG, 15, TimeUnit.MINUTES)
            return it
        }
        return null
    }

    override fun getValue(code: AppConfigCode): String? {
        return getValue(code.code)
    }

    override fun getValueOrDefault(code: AppConfigCode): String? {
        return getValueOrDefault(code, code.defaultValue)
    }

    override fun getValueOrDefault(code: AppConfigCode, defaultValue: String): String? {
        return getValue(code) ?: defaultValue
    }

    override fun removeById(id: Serializable?): Boolean {
        val b = super<AppConfigService>.removeById(id)
        redisTemplate.delete(RedisKeys.APPCONFIG)
        return b
    }

    override fun <T> getConfig(clazz: Class<T>): T {
        val codeList = AppConfigCode.getGroup(clazz)
        val map: MutableMap<String, Any?> = HashMap()
        for (c in codeList) {
            map[c.code] = getValueOrDefault(c)
        }
        return BeanUtil.toBean(map, clazz)
    }

    override fun <T> setConfig(dto: T) {
        redisTemplate.delete(RedisKeys.APPCONFIG)
        val map = BeanUtil.beanToMap(dto, false, true)
        for ((key, value) in map) {
            set(key, value)
        }
        redisTemplate.delete(RedisKeys.APPCONFIG)
    }

    private fun set(code:String, value: Any) {
        val hasCode = count(
            KtQueryWrapper(AppConfig())
                .eq(AppConfig::code, code)
        ) > 0
        if (hasCode) {
            update(
                KtUpdateWrapper(AppConfig())
                    .eq(AppConfig::code, code)
                    .set(AppConfig::updateTime, DateUtil.date())
                    .set(AppConfig::value, if (value is String) value else value.toString() )
            )
        } else {
            val config = AppConfig()
            config.code = code
            config.value = if (value is String) value else value.toString()
            config.createBy = "not"
            config.createTime =LocalDateTime.now()
            save(config)
        }
    }

    override fun set(code: AppConfigCode, value: Any) {
        redisTemplate.delete(RedisKeys.APPCONFIG)
        set(code.code, value)
        redisTemplate.delete(RedisKeys.APPCONFIG)
    }
}
