package com.fund.controller.home

import cn.hutool.core.date.DateUtil
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.conf.mapper.AppConfigMapper
import com.fund.modules.sys.vo.HomeData
import com.fund.modules.sys.vo.UndoData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/home"])
@Tag(name = "首页接口", description = "首页数据")
class HomeController(
    private val appConfigMapper: AppConfigMapper
) {

    @GetMapping(value = ["homeData"])
    fun homeData(): R<HomeData> {
        val date = DateUtil.date()
        val startTime = DateUtil.beginOfDay(date)
        val endTime = DateUtil.endOfDay(date)
        val homeData: HomeData = appConfigMapper.selectHomeData(null, startTime, endTime) ?: throw BusinessException("查询异常")
        return R.success(homeData)
    }

    @GetMapping("/undoData")
    @Operation(summary = "代办项数据")
    fun undoData(): R<UndoData> {
        //val adminId = StpUtil.getLoginIdAsLong()
        //val topId = if (adminId == 1L) null else adminId
        return R.success(appConfigMapper.selectUndoData(null) ?: throw BusinessException("查询异常"))
    }

}