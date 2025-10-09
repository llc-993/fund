package com.fund.controller.ipo

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.modules.ipo.IpoApplyRequest
import com.fund.modules.ipo.IpoUpdateRequest
import com.fund.modules.ipo.model.Ipo
import com.fund.modules.ipo.service.IpoService
import com.fund.modules.ipo.service.StockSubscriptionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/ipo")
class IpoController(
    private val ipoService: IpoService,
    private val stockSubscriptionService: StockSubscriptionService
) {

    @GetMapping("list")
    fun list(): R<Any> {
        val ipos = ipoService.list(
            KtQueryWrapper(Ipo())
                .eq(Ipo::status, 1)
        )
        return R.success(ipos)
    }

    @SaCheckLogin
    @PostMapping("apply")
    fun apply(@RequestBody req: IpoApplyRequest):R<Any> {
        return stockSubscriptionService.apply(req, StpUtil.getLoginIdAsLong())
    }

    @SaCheckLogin
    @GetMapping("history")
    fun history():R<Any> {
        return stockSubscriptionService.history(StpUtil.getLoginIdAsLong())
    }

    @SaCheckLogin
    @PostMapping("update")
    fun update(@RequestBody req: IpoUpdateRequest): R<Any> {
        return stockSubscriptionService.update(req, StpUtil.getLoginIdAsLong())
    }

}