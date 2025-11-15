package com.fund.controller.user

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.user.AdminUserQueryReq
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import com.fund.modules.user.vo.AdminUserVo
import com.fund.modules.wallet.model.AppUserWalletV2
import com.fund.modules.wallet.service.AppUserWalletV2Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: AppUserService,
    private val userPositionService: UserPositionService,
    private val walletV2Service: AppUserWalletV2Service
) {

    @SaCheckLogin
    @GetMapping("/list")
    fun userPage(
        @SwaggerRequestBody(
            description = "查询用户信息",
            required = true
        ) @RequestBody req: AdminUserQueryReq
    ): R<Any> {

        val page = Page<AppUser>(req.pageNum, req.pageSize)

        val page1 = userService.page(
            page, KtQueryWrapper(AppUser::class.java)
                .eq(StrUtil.isNotBlank(req.username), AppUser::userName, req.username)
                .eq(StrUtil.isNotBlank(req.userAccount), AppUser::userAccount, req.userAccount)
                .eq(StrUtil.isNotBlank(req.mobilePhone), AppUser::mobilePhone, req.mobilePhone)
        )

        // 获取所有用户ID列表
        val userIds = page1.records.mapNotNull { it.id }
        
        // 如果有用户记录，则获取每个用户的盈亏总和
        val voList: MutableList<AdminUserVo> = mutableListOf()
        if (userIds.isNotEmpty()) {
            val userProfitMap = userPositionService.getProfitAndLoseByUser(userIds)

            for (user in page1.records) {
                val userVo = AdminUserVo()
                BeanUtil.copyProperties(user, userVo)

                userVo.profitAndLose= userProfitMap[user.id] ?: BigDecimal.ZERO

                user.wallet = walletV2Service.list(KtQueryWrapper(AppUserWalletV2())
                    .eq(AppUserWalletV2::userId, user.id)
                )

                voList.add(userVo)
            }
        }
        val page2 = Page<AdminUserVo> (page1.current, page1.size, page1.total)
        page2.records = voList
        return R.success(page2)
    }



}