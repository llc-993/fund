package com.fund.modules

import com.alibaba.fastjson.JSON
import com.fund.exception.BusinessException
import mu.KotlinLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder


@Component
class BaseRestApi(private val restTemplate: RestTemplate) {
    private val log = KotlinLogging.logger {}

    operator fun get(url: String?, headers: HttpHeaders? = null): String {
        val request = HttpEntity<MultiValueMap<String, Any>?>(null, headers)
        val builder = UriComponentsBuilder.fromHttpUrl(url!!)

        val entity: ResponseEntity<String> = restTemplate.exchange<String>(
            builder.build().toString(), HttpMethod.GET, request,
            String::class.java
        )
        /*if (HttpStatus.OK != entity.statusCode) {
            throw BusinessException("common.base.remote.api.error")
        }*/
        return entity.body ?: ""
    }

    fun post(url: String, data: Any? = null, headers: HttpHeaders? = null): String? {
        val httpEntity = HttpEntity(data, headers)
        //log.info("post params: {}", JSON.toJSONString(data))
        val entity: ResponseEntity<String> = restTemplate.postForEntity(url, httpEntity, String::class.java)
        /*if (HttpStatus.OK != entity.statusCode) {
            throw BusinessException("common.base.remote.api.error")
        }*/
        return entity.body
    }

    fun post(url: String, data: Any? = null, headers: HttpHeaders? = null, vararg ignoreStatus: HttpStatus): String? {
        val httpEntity = HttpEntity(data, headers)
        log.info("post params: {}", JSON.toJSONString(data))
        val entity: ResponseEntity<String> = restTemplate.postForEntity(url, httpEntity, String::class.java)
        var ignore = false
        for (ig in ignoreStatus) {
            if (ig == entity.statusCode) {
                ignore = true
            }
        }
        if (!ignore && HttpStatus.OK != entity.statusCode) {
            throw BusinessException("common.base.remote.api.error")
        }
        return entity.body
    }

    fun postByFormData(url: String, params: MultiValueMap<String, Any>): String? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val requestEntity = HttpEntity(params, headers)
        val response = restTemplate.exchange(
            url, HttpMethod.POST, requestEntity,
            String::class.java
        )
        return response.body
    }
}
