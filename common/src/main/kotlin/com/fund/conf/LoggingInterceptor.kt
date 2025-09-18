package com.fund.conf

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * 拦截器，用于记录请求的日志信息，例如请求路径和方法。
 */
@Component
class LoggingInterceptor : HandlerInterceptor {

    private val logger = KotlinLogging.logger {  }

    /**
     * 在请求处理前记录请求信息。
     *
     * @param request HTTP 请求。
     * @param response HTTP 响应。
     * @param handler 请求的处理程序。
     * @return 返回 true 以继续处理请求。
     */
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        logger.info("Processing request: ${request.method} ${request.requestURI}")
        return true
    }
}