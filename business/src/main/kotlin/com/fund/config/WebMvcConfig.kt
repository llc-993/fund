package com.fund.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web MVC 配置类
 * 配置 API 路径前缀，统一为所有 @RestController 添加 /api 前缀
 */
//@Configuration
class WebMvcConfig : WebMvcConfigurer {

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        // 为所有 @RestController 添加 /api 前缀
        configurer.addPathPrefix("/api") { clazz ->
            // 只为 @RestController 添加前缀
            clazz.isAnnotationPresent(RestController::class.java)
        }
    }
}

