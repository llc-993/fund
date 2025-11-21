package com.fund.conf

import mu.KotlinLogging
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit


@Configuration
class RestTemplateConfig {
    private val log = KotlinLogging.logger {}
    @Bean
    fun connectionPool(): ConnectionPool? {
        // 设置连接池参数，最大空闲连接数200，空闲连接存活时间10s
        return ConnectionPool(200, 10, TimeUnit.SECONDS)
    }

    @Bean
    fun okHttpClient(): OkHttpClient? {
        return OkHttpClient.Builder().retryOnConnectionFailure(false).connectionPool(connectionPool()!!)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.HOURS)
            .writeTimeout(30, TimeUnit.SECONDS).build()
    }

    @Bean
    fun clientHttpRequestFactory(): ClientHttpRequestFactory? {
        return OkHttp3ClientHttpRequestFactory(okHttpClient()!!)
    }


    class ErrorHandler: DefaultResponseErrorHandler() {
        override fun hasError(response: ClientHttpResponse): Boolean {
            val rawStatusCode = response.rawStatusCode
            val statusCode = HttpStatus.resolve(rawStatusCode)
            if (statusCode == HttpStatus.TOO_MANY_REQUESTS) { return false
            }
            return statusCode?.let { hasError(it) } ?: hasError(rawStatusCode)
        }
    }

    /**
     * rest模板
     * @return
     */
    @Bean
    fun restTemplate(clientHttpRequestFactory: ClientHttpRequestFactory?): RestTemplate? {
        // boot中可使用RestTemplateBuilder.build创建
        val restTemplate = RestTemplate()
        // 配置请求工厂
        restTemplate.requestFactory = clientHttpRequestFactory!!

        restTemplate.errorHandler = ErrorHandler()
        // 处理请求中文乱码问题
        val messageConverters = restTemplate.messageConverters
        for (messageConverter in messageConverters) {
            if (messageConverter is StringHttpMessageConverter) {
                messageConverter.defaultCharset = StandardCharsets.UTF_8
            }
            if (messageConverter is MappingJackson2HttpMessageConverter) {
                messageConverter.defaultCharset = StandardCharsets.UTF_8
            }
            if (messageConverter is AllEncompassingFormHttpMessageConverter) {
                messageConverter.setCharset(StandardCharsets.UTF_8)
            }
        }
        return restTemplate
    }

}
