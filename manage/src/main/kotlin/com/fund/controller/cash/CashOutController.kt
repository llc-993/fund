package com.fund.controller.cash

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.agent.model.AppAgentRelation
import com.fund.modules.agent.service.AppAgentRelationService
import com.fund.modules.cash.CashOutEditReq
import com.fund.modules.cash.CashOutOrderQueryPageReq
import com.fund.modules.cash.CashOutReviewReq
import com.fund.modules.sys.service.SysOptLogService
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import com.fund.modules.wallet.model.AppUserCashOutOrder
import com.fund.modules.wallet.service.AppUserCashOutOrderService
import com.fund.modules.wallet.vo.AppUserCashOutOrderVO
import com.fund.utils.DTOUtil
import com.fund.utils.RedisLockService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "提现管理", description = "提现订单查询和管理相关接口")
@RestController
@RequestMapping("/cash/out")
class CashOutController(
    private val cashOutOrderService: AppUserCashOutOrderService,
    private val agentRelationService: AppAgentRelationService,
    private val userService: AppUserService,
    private val optLogService: SysOptLogService,
) {

    @Operation(
        summary = "提现订单列表",
        description = "分页查询提现订单，支持按关键词、订单号、状态、用户组、时间范围等条件筛选"
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = [Content(schema = Schema(implementation = AppUserCashOutOrderVO::class))]
    )
    @GetMapping("/page")
    fun page(req: CashOutOrderQueryPageReq): R<Page<AppUserCashOutOrderVO>> {
        val p: Page<AppUserCashOutOrder> = Page<AppUserCashOutOrder>(req.pageNum, req.pageSize)

        // 根据关键词搜索用户ID列表
        val searchUserIds: List<Long?> = if (StrUtil.isBlank(req.keyword)) {
            emptyList()
        } else {
            userService.list(
                KtQueryWrapper(AppUser())
                    .like(AppUser::keyword, req.keyword)
            ).map { it.id }.toList()
        }

        // 如果使用了关键字搜索，并且没有符合条件的用户id，直接返回空分页
        if (CollUtil.isEmpty(searchUserIds) && StrUtil.isNotBlank(req.keyword)) {
            val emptyPage: Page<AppUserCashOutOrderVO> =
                DTOUtil.buildPage<AppUserCashOutOrderVO, AppUserCashOutOrder>(p)
            return R.success(emptyPage)
        }

        // 构建查询条件
        val queryWrapper = KtQueryWrapper(AppUserCashOutOrder())
            .`in`(CollUtil.isNotEmpty(searchUserIds), AppUserCashOutOrder::userId, searchUserIds)
            .eq(req.cashStatus != null, AppUserCashOutOrder::cashStatus, req.cashStatus)
            .eq(StrUtil.isNotBlank(req.orderNo), AppUserCashOutOrder::orderNo, req.orderNo)
            .eq(req.userGroup != null, AppUserCashOutOrder::userGroup, req.userGroup)

        // 处理时间范围查询
        req.startTime?.takeIf { StrUtil.isNotBlank(it) }?.let { startTimeStr ->
            try {
                val startTime = LocalDateTime.parse(startTimeStr)
                queryWrapper.gt(AppUserCashOutOrder::applyTime, startTime)
            } catch (e: Exception) {
                // 如果解析失败，忽略该条件
            }
        }

        req.endTime?.takeIf { StrUtil.isNotBlank(it) }?.let { endTimeStr ->
            try {
                val endTime = LocalDateTime.parse(endTimeStr)
                queryWrapper.le(AppUserCashOutOrder::applyTime, endTime)
            } catch (e: Exception) {
                // 如果解析失败，忽略该条件
            }
        }

        queryWrapper.orderByDesc(AppUserCashOutOrder::applyTime)

        val page: Page<AppUserCashOutOrder> = cashOutOrderService.page(p, queryWrapper)

        // 转换为VO并填充扩展字段
        val voList: MutableList<AppUserCashOutOrderVO> = ArrayList()

        for (order in page.records) {
            val vo: AppUserCashOutOrderVO = DTOUtil.toDTO<AppUserCashOutOrderVO, AppUserCashOutOrder>(
                order,
                AppUserCashOutOrderVO::class.java
            ) ?: continue

            // 查询代理关系信息
            order.userId?.let { userId ->
                val ar: AppAgentRelation? = agentRelationService.getOne(
                    KtQueryWrapper(AppAgentRelation())
                        .eq(AppAgentRelation::oriUserId, userId)
                        .last("limit 1")
                )

                ar?.let {
                    vo.parentId = it.p1Id
                    vo.parentAccount = it.p1Account

                    // 查询一级代理的手机号
                    it.p1Id?.let { p1Id ->
                        val parent: AppUser? = userService.getById(p1Id)
                        if (parent != null && StrUtil.isNotBlank(parent.mobilePhone)) {
                            vo.parentMobilePhone = parent.mobilePhone
                        }
                    }
                }

                // 查询用户注册手机号
                val user: AppUser? = userService.getById(userId)
                vo.registerPhone = user?.mobilePhone ?: ""

                // 计算今日提现次数
                val today = java.util.Date()
                val startTime = DateUtil.beginOfDay(today)
                val startLocalDateTime = java.time.LocalDateTime.ofInstant(
                    startTime.toInstant(),
                    java.time.ZoneId.systemDefault()
                )
                vo.cashOutCountToday = cashOutOrderService.count(
                    KtQueryWrapper(AppUserCashOutOrder())
                        .eq(AppUserCashOutOrder::userId, userId)
                        .gt(AppUserCashOutOrder::applyTime, startLocalDateTime)
                )

            }

            voList.add(vo)
        }

        val voPage: Page<AppUserCashOutOrderVO> = DTOUtil.buildPage<AppUserCashOutOrderVO, AppUserCashOutOrder>(page)
        voPage.records = voList
        return R.success(voPage)
    }

    @PostMapping("/edit")
    @Operation(summary = "编辑提现订单，和刷单一样")
    fun edit(@RequestBody @Validated req: CashOutEditReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        RedisLockService.transaction {
            cashOutOrderService.getById(req.id) ?: throw BusinessException("找不到提现订单")
            cashOutOrderService.update(
                KtUpdateWrapper(AppUserCashOutOrder())
                    .eq(AppUserCashOutOrder::id, req.id)
                    .set(AppUserCashOutOrder::address, req.address)
            )
        }

        optLogService.addLog(adminId, "编辑提现订单", JSON.toJSONString(req))
        return R.success()
    }

    @PostMapping("/review")
    @Operation(summary = "提现审核，和刷单的一样")
    fun review(@RequestBody @Validated req: CashOutReviewReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        cashOutOrderService.review(adminId, req) block@{
            val appUser = userService.getById(it.userId) ?: return@block
            // 发送邮件

        }
        optLogService.addLog(adminId, "提现审核", JSON.toJSONString(req))

        return R.success()
    }


}