package com.fund.conf


import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
@EnableCaching
class RedisConfig () : CachingConfigurer{

    @Value("\${spring.data.redis.host}")
    var host: String? = null
    @Value("\${spring.data.redis.port}")
    var port: Int? = null
    @Value("\${spring.data.redis.password:}")
    var password: String? = null
    @Value("\${spring.data.redis.database:0}")
    var database: Int? = null


    @Bean
    fun cacheManager(lettuceConnectionFactory: LettuceConnectionFactory): CacheManager? {
        val builder = RedisCacheManagerBuilder.fromConnectionFactory(lettuceConnectionFactory)
        val  cachenames = object : HashSet<String> () {
            init {
                add("codeNameCache")
            }
        }
        builder.initialCacheNames(cachenames)
        return builder.build()
    }

    @Bean
    fun redisTemplate(lettuceConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Any>? {
        val objectMapper = ObjectMapper()
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)

        val jackson2JsonRedisSerializer: Jackson2JsonRedisSerializer<*> = Jackson2JsonRedisSerializer(objectMapper,Any::class.java)
        // 配置 redisTemplate
        val redisTemplate = RedisTemplate<String, Any> ()
        redisTemplate.connectionFactory = lettuceConnectionFactory

        val stringSerializer: RedisSerializer<*> = StringRedisSerializer()

        redisTemplate.keySerializer = stringSerializer
        redisTemplate.valueSerializer = jackson2JsonRedisSerializer
        redisTemplate.hashKeySerializer = stringSerializer
        redisTemplate.hashValueSerializer = jackson2JsonRedisSerializer
        redisTemplate.afterPropertiesSet()

        return redisTemplate
    }

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration()
        config.hostName= host!!
        config.port  = port!!
        config.database = database!!
        if (password != null) {
            config.password = RedisPassword.of(password!!)
        }
        return LettuceConnectionFactory(config)
    }
}