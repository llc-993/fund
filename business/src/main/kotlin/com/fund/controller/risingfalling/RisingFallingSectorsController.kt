package com.fund.controller.risingFalling

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.modules.risingFalling.RisingFallingSectorsApplyRequest
import com.fund.modules.risingFalling.RisingFallingSectorsUpdateRequest
import com.fund.modules.risingFalling.model.RisingFallingSectors
import com.fund.modules.risingFalling.service.RisingFallingSectorsService
import com.fund.modules.risingFalling.service.RisingFallingSectorsSubscriptionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 涨跌板块
 */
@RestController
@RequestMapping("/risingFallingSectors")
class RisingFallingSectorsController(
    private val risingFallingSectorsService: RisingFallingSectorsService,
    private val risingFallingSectorsSubscriptionService: RisingFallingSectorsSubscriptionService
) {

    /**
     * 列表
     */
    @GetMapping("list")
    fun list(): R<Any> {
        val risingFallingSectors = risingFallingSectorsService.list(
            KtQueryWrapper(RisingFallingSectors())
                .eq(RisingFallingSectors::displayStatus, 0)  // 0=显示
        )
        return R.success(risingFallingSectors)
    }

    /**
     * 申购
     */
    @SaCheckLogin
    @PostMapping("apply")
    fun apply(@RequestBody req: RisingFallingSectorsApplyRequest): R<Any> {
        return risingFallingSectorsSubscriptionService.apply(req, StpUtil.getLoginIdAsLong())
    }

    /**
     * 申购历史
     */
    @SaCheckLogin
    @GetMapping("history")
    fun history(): R<Any> {
        return risingFallingSectorsSubscriptionService.history(StpUtil.getLoginIdAsLong())
    }

    /**
     * 修改申购
     */
    @SaCheckLogin
    @PostMapping("update")
    fun update(@RequestBody req: RisingFallingSectorsUpdateRequest): R<Any> {
        return risingFallingSectorsSubscriptionService.update(req, StpUtil.getLoginIdAsLong())
    }

}
