package com.fund.conf

import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig {

    private var log = KotlinLogging.logger {  }

    @Value(value = "\${spring.data.redis.host:}")
    private val host: String? = null

    @Value(value = "\${spring.data.redis.port:}")
    private val port: String? = null

    @Value(value = "\${spring.data.redis.password:}")
    private val password: String? =null

    @Value(value = "\${spring.data.redis.database:}")
    private val database: Int? = null

    @Bean
    fun redissonClient(): RedissonClient? { // 单机配置
        log.info("加载redisson配置……")
        val config = Config()
        val singleServer = config.useSingleServer()
        singleServer.address = "redis://$host:$port"
        if (StringUtils.isNoneBlank(password)) {
            singleServer.password = password
        }
        return Redisson.create(config)
    }

}