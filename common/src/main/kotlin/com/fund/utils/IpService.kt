package com.fund.utils


import cn.hutool.core.net.NetUtil
import cn.hutool.core.util.StrUtil
import com.fund.common.RedisKeys
import com.fund.exception.BusinessException
import mu.KotlinLogging
import org.lionsoul.ip2region.xdb.Searcher
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component


@Component
class IpService(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val log = KotlinLogging.logger {  }
    private val retryTemplate = RetryTemplate()
    init {
        // 设置退避策略: 固定间隔时间重试
        val backoff = FixedBackOffPolicy()
        backoff.backOffPeriod = 2000
        retryTemplate.setBackOffPolicy(backoff)

        // 配置重试次数
        val retryPolicy = SimpleRetryPolicy()
        retryPolicy.maxAttempts = 3
        retryTemplate.setRetryPolicy(retryPolicy)
    }

    private fun isValidIPv4(ip: String): Boolean {
        val pattern = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$".toRegex()
        return pattern.matches(ip)
    }

    fun getRealAddressByIP(ip: String, force: Boolean = false): String? {
        val rst = retryTemplate.execute<String?, BusinessException>({
            if (it.retryCount > 1) {
                log.info { "获取IP归属地-当前重试次数: ${it.retryCount}" }
            }
            _getRealAddressByIP(ip, force)
        }) {
            log.warn("获取IP归属地-重试次数耗尽: ${it.retryCount}", it.lastThrowable)
            null
        }
        return rst
    }

    fun _getRealAddressByIP(ip: String, force: Boolean = false): String? {
        var value: String? = if (force) null else byCache(ip)
        if (value == null || StrUtil.isBlank(value)) {

            value = getAddress(ip)
            if (value != null)
                redisTemplate.opsForHash<String, String>().put(RedisKeys.IP_CACHES, ip, value)
        }
        return value
    }

    private fun byCache(ip: String): String? {
        return redisTemplate.opsForHash<String, String>().get(RedisKeys.IP_CACHES, ip)
    }


    private val cBuff by lazy {
        try {
            Searcher.loadContentFromFile("../ip2region.xdb").also {
                log.info("IP数据库初始化完成")
            }
        } catch (e: Exception) {
            log.error("加载IP数据库失败", e)
            null
        }
    }

    fun getAddress(ip: String?): String? {
        if (ip!!.isBlank() || NetUtil.isUnknown(ip)) return null

        return cBuff?.let { buffer ->
            try {
                var searcher = Searcher.newWithBuffer(buffer)
                return searcher.search(ip)
            } catch (e: Exception) {
                log.error("IP查询失败（IP: $ip）", e)
                null
            }
        }
    }

}
