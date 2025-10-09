package com.fund.job

import cn.hutool.core.util.ObjectUtil
import cn.hutool.http.Header
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.config.ApiConfig
import com.fund.enetity.IpoData
import com.fund.enetity.JsonBean
import com.fund.modules.ipo.model.Ipo
import com.fund.modules.ipo.service.IpoService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Date

@Component
class IpoJob(
    private val apiConfig: ApiConfig,
    private val ipoService: IpoService
) {


    // @Scheduled(cron = "0 * * * * *")
    fun loadIpoData() {
        val url = apiConfig.stock.getIpoDataUrl("india")
        
        // 发起 HTTP 请求获取 IPO 数据
        val responseBody = HttpUtil.createGet(url)
            .header(Header.AUTHORIZATION, apiConfig.stock.authorization)
            .execute()
            .body()

        // 解析 JSON 数据并处理
        val ipoList = JSON.parseArray(responseBody, JsonBean::class.java) ?: return
        
        ipoList.forEach { ipoBean ->
            val ipoData = JSON.parseObject(ipoBean.msg, IpoData::class.java)

            // 查询数据库中是否已存在该 IPO
            val existingIpo = ipoService.getOne(
                KtQueryWrapper(Ipo())
                    .eq(Ipo::country, ipoData.country)
                    .eq(Ipo::name, ipoData.name)
                    .last(" limit 1 ")
            )

            // 构建 IPO 对象
            val ipo = Ipo().apply {
                country = ipoData.country
                name = ipoData.name
                symbol = ipoData.symbol
                count = ipoData.issueSize?.toLong()
                price = ipoData.issuePrice?.toBigDecimal()
                closeDate = ipoData.closeDate.toLong()
                listingDate = ipoData.listingDate.toLong()
                openDate = ipoData.openDate.toLong()
                createTime = LocalDateTime.now()
                status = if (symbol != null) 2 else 1
            }

            // 保存或更新 IPO 数据
            if (ObjectUtil.isEmpty(existingIpo)) {
                ipoService.save(ipo)
            } else {
                ipo.id = existingIpo.id
                ipoService.updateById(ipo)
            }
        }
    }

}