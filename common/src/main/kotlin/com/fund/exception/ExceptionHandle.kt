package com.fund.exception

import cn.dev33.satoken.exception.NotLoginException
import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.CharsetUtil
import cn.hutool.json.JSONUtil
import com.fund.common.Constants
import com.fund.common.entity.R
import com.fund.utils.I18nUtil
import com.fund.utils.IpUtils
import mu.KotlinLogging
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import jakarta.servlet.http.HttpServletRequest
import java.nio.charset.Charset


@RestControllerAdvice
class ExceptionHandle(
    private val i18nUtil: I18nUtil,
) {

    private val log = KotlinLogging.logger {}
    @ExceptionHandler(value = [Exception::class])
    fun handle(e: Exception, request: HttpServletRequest): R<Unit> {
        log.error("${request.requestURI} , 全局异常Exception：${handleLog(request)}" , e)
        // 禁止直接返回异常信息，有安全风险！！！
        //return R.error(e.message!!)
        return R.error(i18nUtil.getMessage(Constants.fail, request))
    }

    @ExceptionHandler(value = [MaxUploadSizeExceededException::class])
    fun handle(e: MaxUploadSizeExceededException, request: HttpServletRequest): R<Unit> {
        log.error( "${request.requestURI} , MaxUploadSizeExceededException:{e.message}", e)
        val msg = i18nUtil.getMessage("uploaded_file_too_large_error", request)
        return R.error( msg, R.HttpStatus.ERROR)
    }

    @ExceptionHandler(value = [BusinessException::class])
    fun businessException(e: BusinessException, request: HttpServletRequest): R<Unit> {
        log.error("${request.requestURI} , BusinessException: ${e.message}" )
        val msg = i18nUtil.getMessage(e.message ?: "", request)
        return R.error(msg, e.code)
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun validation(e: MethodArgumentNotValidException, request: HttpServletRequest): R<Unit>? {
        log.error( "${request.requestURI} , validationException: ${e.message}")
        val it = e.bindingResult.allErrors[0].defaultMessage ?: ""
        val msg = i18nUtil.getMessage(it, request)
        return R.error(msg, 500)
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(
        e: HttpRequestMethodNotSupportedException?,
        request: HttpServletRequest
    ): R<Unit> {
        log.error("${request.requestURI } ,不支持当前请求方法", e)
        val msg = i18nUtil.getMessage("common.exception.notsupportmethod", request)
        return R.error(msg, HttpStatus.METHOD_NOT_ALLOWED.value())
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        e: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): R<Unit> {
        log.error("${request.requestURI},参数解析失败：${handleLog(request)}", e)
        return R.error( e.message ?: "", HttpStatus.BAD_REQUEST.value())
    }

    @ExceptionHandler(value = [NotLoginException::class])
    fun handleNotLoginException(e: NotLoginException): R<Unit> {
        return when(e.type) {
            NotLoginException.NOT_TOKEN -> R.error("Not logged in", 401)
            // token 无效
            NotLoginException.INVALID_TOKEN -> R.error("Invalid session", 401)
            // token 已过期
            NotLoginException.TOKEN_TIMEOUT -> R.error("Session expired", 401)
            // 已被顶下线
            NotLoginException.BE_REPLACED -> R.error("The session was pushed offline", 401)
            // token 已被踢下线
            NotLoginException.KICK_OUT -> R.error("The session has been kicked off", 401)
            NotLoginException.TOKEN_FREEZE -> R.error("The session is frozen", 401)
            NotLoginException.NO_PREFIX -> R.error("Not submitted with the specified prefix token", 401)
            else -> R.error("", 401)
        }
    }


    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(
        e: MissingServletRequestParameterException,
        request: HttpServletRequest
    ): R<Unit> {
        log.error("${request.requestURI},参数解析失败：${handleLog(request)}", e)
        return R.error(e.message, HttpStatus.BAD_REQUEST.value())
    }

    fun handleLog(request: HttpServletRequest): String? {
        var str: String? = null
        try {
            str = getHeaderFromRequest(request)
        } catch (e: Exception) {
            log.error("", e)
        }
        return str
    }

    //从request中获取header
    fun getHeaderFromRequest(request: HttpServletRequest): String? {
        // 获取请求头
        val enumeration = request.headerNames
        val headers = StringBuffer()
        while (enumeration.hasMoreElements()) {
            val name = enumeration.nextElement()
            val value = request.getHeader(name)
            headers.append("$name=$value").append("; ")
        }
        val requestParam = getParamFromRequest(request)

        val body = try {
            val json = IoUtil.read(request.inputStream, Charset.forName(CharsetUtil.UTF_8) )
            json
        } catch (e: Exception) {
            log.error { "从HttpServletRequest中获取请求体异常 ${e.message}" }
        }
        //获取当前请求的语言
        val locale = LocaleContextHolder.getLocale()
        val lang = locale.language + "_" + locale.country
        //获取IP
        val ipAddr: String? = IpUtils.getIpAddr(request)
        //log.debug { "请求ip : $ipAddr" }

        //log.debug { "请求语言 : ${lang}" }
        return """
            【请求头】：${headers}
            【请求URI】：${request.requestURI}
            【语言】: $lang
            【请求IP】：${ipAddr}
            【请求参数】：$requestParam
            【请求体】：$body
        """.trimIndent()
    }

    //从request中获取请求参数
    fun getParamFromRequest(request: HttpServletRequest): String {
        val parameterMap = request.parameterMap ?: return JSONUtil.createObj().toJSONString(0)
        return JSONUtil.parse(parameterMap).toJSONString(0)
    }

}
