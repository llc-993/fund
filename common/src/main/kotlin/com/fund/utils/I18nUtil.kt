package com.fund.utils


import com.fund.common.HeaderConstants
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.Locale
import jakarta.servlet.http.HttpServletRequest

/**
 * 工具类，用于在 Spring Boot 应用程序中处理国际化（i18n）。
 * 提供方法通过 Spring 的 MessageSource 获取本地化消息。
 */
@Component
class I18nUtil(
    private val messageSource: MessageSource
) {

    /**
     * 使用当前区域设置获取指定键的本地化消息。
     *
     * @param key 资源文件中查找的消息键。
     * @param defaultMessage 如果键未找到时的默认消息（可选）。
     * @param args 用于替换消息中占位符的可选参数。
     * @return 本地化消息，如果未找到则返回默认消息或键。
     */
    fun getMessage(key: String, defaultMessage: String? = null, vararg args: Any?): String {
        return try {
            messageSource.getMessage(key, args, LocaleContextHolder.getLocale())
        } catch (e: Exception) {
            defaultMessage ?: key
        }
    }

    /**
     * 使用指定区域设置获取指定键的本地化消息。
     *
     * @param key 资源文件中查找的消息键。
     * @param locale 使用的特定区域设置。
     * @param defaultMessage 如果键未找到时的默认消息（可选）。
     * @param args 用于替换消息中占位符的可选参数。
     * @return 本地化消息，如果未找到则返回默认消息或键。
     */
    fun getMessage(key: String, locale: Locale, defaultMessage: String? = null, vararg args: Any?): String {
        return try {
            messageSource.getMessage(key, args, locale)
        } catch (e: Exception) {
            defaultMessage ?: key
        }
    }

    fun getMessage(code: String, request: HttpServletRequest? = null): String {
        val local = resolveLocale(request ?: getRequest())
        return try {
            messageSource.getMessage(code, null, local)
        } catch (_: Exception){ code }
    }
    private fun getRequest(): HttpServletRequest {
        val attr = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        return attr.request as HttpServletRequest
    }

    companion object {
        private const val defaultI18nCode = "en"
    }

    fun resolveLocale(request: HttpServletRequest): Locale {
        return resolveLocale(request.getHeader(HeaderConstants.Lang) ?: defaultI18nCode)
    }
    fun resolveLocale(localeValue: String): Locale {
        // en-US -> en_US
        val replaced = localeValue.replace("-","_")
        return StringUtils.parseLocale(replaced) ?: Locale.US
    }
}