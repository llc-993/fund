package com.fund.controller.user

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.user.AdminUserQueryReq
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: AppUserService,
    private val userPositionService: UserPositionService
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
        if (userIds.isNotEmpty()) {
            val userProfitMap = userPositionService.getProfitAndLoseByUser(userIds)
            
            // 在返回结果中添加用户盈亏信息
            val userList = page1.records.map { user ->
                val userId = user.id
                val profit = if (userId != null) userProfitMap[userId] ?: java.math.BigDecimal.ZERO else java.math.BigDecimal.ZERO
                
                // 创建包含用户信息和盈亏数据的Map
                mapOf(
                    "user" to user,
                    "profitAndLose" to profit
                )
            }
            
            // 更新分页对象中的记录
            val result = Page<Map<String, Any>>(page1.current, page1.size, page1.total)
            result.records = userList
            
            return R.success(result)
        }

        return R.success(page1)
    }

}