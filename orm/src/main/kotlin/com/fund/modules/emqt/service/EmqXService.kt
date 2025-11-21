package com.fund.modules.emqt.service

import com.fund.modules.emqt.co.MqttMsg

interface EmqXService {

    fun publish(msg: MqttMsg)

}