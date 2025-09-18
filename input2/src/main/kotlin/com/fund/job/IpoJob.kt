package com.fund.job

import cn.hutool.http.Header
import cn.hutool.http.HttpUtil
import com.fund.config.ApiConfig
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class IpoJob(
    private val apiConfig: ApiConfig,
) {


   // @Scheduled(cron = "0 * * * * *")
    fun loadIpoData () {
        val str = apiConfig.stock.getIpoDataUrl("india")

        val httpRequest = HttpUtil.createGet(str)

        httpRequest.header(Header.AUTHORIZATION, apiConfig.stock.authorization)

        val text = httpRequest.execute().body()
        println(text)

    }

}