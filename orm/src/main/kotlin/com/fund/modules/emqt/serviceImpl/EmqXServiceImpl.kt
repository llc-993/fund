package com.fund.modules.emqt.serviceImpl

import cn.hutool.core.codec.Base64
import cn.hutool.core.util.StrUtil
import com.fund.modules.BaseRestApi
import com.fund.modules.conf.dto.EmqxConfig
import com.fund.modules.conf.enum.AppConfigCode
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.emqt.co.MqttMsg
import com.fund.modules.emqt.service.EmqXService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class EmqXServiceImpl(
    private val restApi: BaseRestApi,
    private val configService: AppConfigService
) : EmqXService {
    companion object {
        private val log = KotlinLogging.logger {}

        private val coroutineScope = CoroutineScope(Dispatchers.IO)

        private const val API_PUBLISH_PATH = "/api/v5/publish"
    }


    fun doPublish(msg: MqttMsg) {
        try {
            val enableValue: String = configService.getValueOrDefault(AppConfigCode.EMQX_ENABLE) ?: "false"
            val enable = StrUtil.equals(enableValue, "true")
            if (!enable) {
                log.warn("未开启 project.emqx.enable")
                return
            }
            val config: EmqxConfig = configService.getConfig(EmqxConfig::class.java)
            val apiHost: String = config.emqxApiHost!!
            val apiPort: Int = config.emqxApiPort!!
            val apiKey: String = config.emqxApiKey!!
            val apiSecret: String = config.emqxApiSecret!!
            val apiUrl = "$apiHost:$apiPort$API_PUBLISH_PATH"
            val basicAuth = "Basic " + Base64.encode("$apiKey:$apiSecret", "UTF8")
            val headers = HttpHeaders()
            headers["authorization"] = basicAuth
            // 忽略 HttpStatus.ACCEPTED 202 状态，默认HttpStatus.OK 200 和 HttpStatus.ACCEPTED 为成功
            val resp: String? = restApi.post(apiUrl, msg, headers, HttpStatus.ACCEPTED)
            log.info("resp: {}", resp)
        } catch (e: Exception) {
            log.error("emqx异常", e)
        }
    }

    override fun publish(msg: MqttMsg) {
        coroutineScope.launch { doPublish(msg) }
    }
}