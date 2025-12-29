package com.fund.controller.cash

import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.fund.common.entity.R
import com.fund.modules.agent.service.AppAgentRelationService
import com.fund.modules.cash.BindPayAddressReq
import com.fund.modules.platform.model.AppPayPlatform
import com.fund.modules.platform.model.AppPayPlatformUser
import com.fund.modules.platform.service.AppPayPlatformService
import com.fund.modules.platform.service.AppPayPlatformUserService
import com.fund.modules.user.service.AppUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.Date
import java.util.stream.Collectors
import kotlin.code


@Tag(name = "支付平台")
@RestController
@RequestMapping(value = ["/payplatform"])
class PayPlatformController(
    private val payPlatformService: AppPayPlatformService,
    private val payPlatformUserService: AppPayPlatformUserService,
    private val appUserService: AppUserService,
    private val agentRelationService: AppAgentRelationService,
) {

    @GetMapping("/list")
    @Operation(summary = "支付平台列表")
    fun list(): R<List<AppPayPlatform>> {
        val list = payPlatformService.list(
            KtQueryWrapper(AppPayPlatform())
                // 状态 0-关闭 1-开启
                .eq(AppPayPlatform::status, 1)
                .orderByDesc(AppPayPlatform::sortBy, AppPayPlatform::id, AppPayPlatform::createTime)
        )
        return R.success(list)
    }


    @GetMapping("/bindList")
    @Operation(summary = "用户绑定的渠道地址列表")
    fun bindList(): R<List<AppPayPlatformUser>> {
        val userId = StpUtil.getLoginIdAsLong()
       // val appUser = appUserService.getById(userId)
        val addressList = payPlatformUserService.list(
            KtQueryWrapper(AppPayPlatformUser())
                .eq(AppPayPlatformUser::userId, userId)
                .orderByDesc(AppPayPlatformUser::createTime, AppPayPlatformUser::id)
        )
      /*  val addressMap = addressList.stream().collect(Collectors.toMap(AppPayPlatformUser::platformCode, AppPayPlatformUser::address) { _, n -> n})

        val platformList = payPlatformService.list()
        for (platform in platformList) {
            if (addressMap[platform.code] == null) {
                val add = AppPayPlatformUser()
                add.userId = userId
                add.userGroup = appUser.userGroup
                add.userAccount = appUser.userAccount
                add.topUserId = agentRelationService.getTopIdByUserIdFromCache(userId)
                add.platformCode = platform.code
                add.address = ""
                addressList.add(add)
                payPlatformUserService.save(add)
            }
        }
        val platformMap = platformList.stream().collect(Collectors.toMap(AppPayPlatform::code, AppPayPlatform::platformName))
        for (address in addressList) {
            address.platformName = platformMap[address.platformCode]
        }*/
        return R.success(addressList)
    }

    @PostMapping("/bind")
    @Operation(summary = "绑定渠道地址或者修改用户的绑定")
    fun bind(@RequestBody @Validated req: BindPayAddressReq): R<Unit> {
        val userId = StpUtil.getLoginIdAsLong()
        val old = payPlatformUserService.getOne(
            KtQueryWrapper(AppPayPlatformUser())
                .eq(AppPayPlatformUser::userId, userId)
                .eq(AppPayPlatformUser::platformCode, req.code)
                .last("limit 1")
        )
        if (old != null) {
            payPlatformUserService.update(
                KtUpdateWrapper(AppPayPlatformUser())
                    .eq(AppPayPlatformUser::id, old.id)
                    .set(AppPayPlatformUser::address, req.address)
                    .set(AppPayPlatformUser::platformCode, req.code)
            )
        } else {
            val appUser = appUserService.getById(userId)
            val platformUser = AppPayPlatformUser()
            platformUser.userId = userId
            platformUser.platformCode = req.code
            platformUser.address = req.address
            platformUser.userGroup = appUser.userGroup
            platformUser.userAccount = appUser.userAccount
            platformUser.topUserId = agentRelationService.getTopIdByUserIdFromCache(userId)
            platformUser.createTime = LocalDateTime.now()
            platformUser.deleted = false
            payPlatformUserService.save(platformUser)
        }

        return R.success()
    }

}