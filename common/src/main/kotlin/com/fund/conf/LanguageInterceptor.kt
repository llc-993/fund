package com.fund.conf


import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.LocaleResolver
import java.util.*

/**
 * 拦截器，用于根据 Accept-Language 头设置用户的首选语言（区域设置）。
 * 区域设置将存储在 LocaleContextHolder 中，并可通过 LocaleResolver 持久化。
 */
@Component
class LanguageInterceptor(
    private val localeResolver: LocaleResolver
) : HandlerInterceptor {

    /**
     * 在请求处理前拦截，提取并设置 Accept-Language 头中的区域设置。
     *
     * @param request HTTP 请求。
     * @param response HTTP 响应。
     * @param handler 请求的处理程序。
     * @return 返回 true 以继续处理请求。
     */
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val acceptLanguage = request.getHeader("Accept-Language")
        val locale = parseAcceptLanguage(acceptLanguage) ?: Locale.ENGLISH
        LocaleContextHolder.setLocale(locale)
        localeResolver.setLocale(request, response, locale)
        return true
    }

    /**
     * 解析 Accept-Language 头，提取主要区域设置。
     *
     * @param acceptLanguage Accept-Language 头的值（例如 "en-US,fr;q=0.9"）。
     * @return 解析出的区域设置，如果头无效或缺失则返回 null。
     */
    private fun parseAcceptLanguage(acceptLanguage: String?): Locale? {
        if (acceptLanguage.isNullOrBlank()) return null
        return try {
            val languageRanges = Locale.LanguageRange.parse(acceptLanguage)
            Locale.lookup(languageRanges, availableLocales())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 返回应用程序支持的区域设置列表。
     * 根据资源文件自定义此列表。
     */
    private fun availableLocales(): List<Locale> = listOf(
        Locale.ENGLISH,
        Locale.SIMPLIFIED_CHINESE
    )
}