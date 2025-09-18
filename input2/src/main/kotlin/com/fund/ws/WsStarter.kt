package com.fund.ws

import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class WsStarter(
    private val wsClient: WsClient,
) {

    private val log = KotlinLogging.logger {}

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        start()
    }

     fun start() {
       try {
           log.info("start ws server")
           wsClient.init()
       } catch (e: Exception) {
           log.error("Exception occurred while starting the ws server", e)
       }
    }
}