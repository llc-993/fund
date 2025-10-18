package com.fund.controller.cash

import com.fund.common.entity.R
import com.fund.modules.cash.CashOutReq
import com.fund.modules.wallet.service.AppUserCashOutOrderService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/withdraw"])
class WithdrawController(
    private val appUserCashOutOrderService: AppUserCashOutOrderService
) {


    /**
     * 提现申请
     */
    @PostMapping("/request")
    fun request(@RequestBody @Validated req: CashOutReq): R<Any> {

    }

}