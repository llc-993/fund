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
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class EmqXServiceImpl(
    private val configService: AppConfigService,
    private val restApi: BaseRestApi,
) : EmqXService {
    companion object {
        private val log = KotlinLogging.logger {}

        private val coroutineScope = CoroutineScope(Dispatchers.IO)

        // 存储 MQTT 客户端实例，key 为 broker URL
        private val mqttClients = ConcurrentHashMap<String, MqttClient>()

        private const val API_PUBLISH_PATH = "/api/v5/publish"
    }


    /**
     * 创建 MQTT 连接选项
     */
    private fun createMqttConnectOptions(config: EmqxConfig): MqttConnectOptions {
        return MqttConnectOptions().apply {
            isCleanSession = true
            isAutomaticReconnect = true
            connectionTimeout = 30
            keepAliveInterval = 60

            // 如果配置了用户名和密码，则设置认证信息
            config.emqxApiKey?.let { userName = it }
            config.emqxApiSecret?.let { password = it.toCharArray() }
        }
    }

    /**
     * 获取或创建 MQTT 客户端
     */
    private fun getOrCreateMqttClient(config: EmqxConfig): MqttClient? {

        val brokerHost = "192.168.3.112"
        val brokerPort = config.emqxMqttPort ?: 1883
        val brokerUrl = "tcp://$brokerHost:$brokerPort"

        val client = mqttClients.computeIfAbsent(brokerUrl) {
            val clientId = "emqx_publisher_${System.currentTimeMillis()}"
            val newClient = MqttClient(brokerUrl, clientId, MemoryPersistence())
            val options = createMqttConnectOptions(config)

            newClient.connect(options)
            log.info("MQTT 客户端连接成功: $brokerUrl")
            newClient
        }

        // 如果客户端已存在但未连接，尝试重连
        if (!client.isConnected) {
            try {
                val options = createMqttConnectOptions(config)
                client.connect(options)
                log.info("MQTT 客户端重连成功: $brokerUrl")
            } catch (e: Exception) {
                log.error(e) { "MQTT 客户端重连失败: $brokerUrl" }
                mqttClients.remove(brokerUrl)
                return null
            }
        }

        return client

    }

    fun doPublish(msg: MqttMsg) {
        try {
            val enableValue: String = configService.getValueOrDefault(AppConfigCode.EMQX_ENABLE) ?: "false"
            val enable = StrUtil.equals(enableValue, "true")
            if (!enable) {
                log.warn("未开启 project.emqx.enable")
                return
            }
            try {
                val config: EmqxConfig = configService.getConfig(EmqxConfig::class.java)
                val client = getOrCreateMqttClient(config)

                if (client == null || !client.isConnected) {
                    log.warn("MQTT 客户端未连接，无法发布消息")
                    return
                }

                val message = MqttMessage(msg.payload.toByteArray())
                message.qos = 1 // QoS 1: 至少一次送达
                message.isRetained = false

                client.publish(msg.topic, message)
                log.debug("MQTT 消息发布成功: topic=${msg.topic}")
            } catch (e: Exception) {
                log.error(e) {"异常："}

                this.pushAPi(msg)
            }
        } catch (e: MqttException) {
            log.error(e) { "MQTT 发布消息异常: topic=${msg.topic}" }
            // 如果连接断开，移除客户端实例，下次重新创建
            if (e.reasonCode == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED.toInt()) {
                mqttClients.values.removeIf { !it.isConnected }
            }
        } catch (e: Exception) {
            log.error(e) { "发布消息异常: topic=${msg.topic}" }
        }
    }

    override fun publish(msg: MqttMsg) {
        try {
            coroutineScope.launch { doPublish(msg) }
        } catch (e: Exception) {
            log.error(e) { "启动发布协程失败" }
        }
    }


    fun pushAPi(msg: MqttMsg) {
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
            val apiUrl = apiHost + ":" + apiPort + API_PUBLISH_PATH
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

}