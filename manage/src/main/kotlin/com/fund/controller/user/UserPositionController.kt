package com.fund.controller.user

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.AdminPositionRequest
import com.fund.modules.ForceClosePositionRequest
import com.fund.modules.UpdateLockStatusRequest
import com.fund.modules.stock.model.UserPosition
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "后台持仓查询", description = "管理员查询用户持仓数据")
@RestController
@RequestMapping("/position")
class UserPositionController(
    private val userPositionService: UserPositionService,
    private val appUserService: AppUserService
) {

    @Operation(summary = "持仓列表", description = "支持按账号、昵称、状态及买入时间区间查询，按状态+ID倒序")
    @PostMapping("/list")
    fun list(@RequestBody req: AdminPositionRequest): R<List<UserPosition>> {
        val wrapper = KtQueryWrapper(UserPosition())

        // 根据账号、昵称筛选用户ID
        if (!req.account.isNullOrBlank() || !req.username.isNullOrBlank()) {
            val userWrapper = KtQueryWrapper(AppUser())
                .eq(!req.account.isNullOrBlank(), AppUser::userAccount, req.account)
                .like(!req.username.isNullOrBlank(), AppUser::userName, req.username)

            val users = appUserService.list(userWrapper)
            if (users.isEmpty()) {
                return R.success(emptyList())
            }
            wrapper.`in`(UserPosition::userId, users.mapNotNull { it.id })
        }

        wrapper.eq(!req.status.isNullOrBlank(), UserPosition::status, req.status)
            .ge(req.startTime != null, UserPosition::buyOrderTime, req.startTime)
            .le(req.endTime != null, UserPosition::buyOrderTime, req.endTime)
            .orderByDesc(UserPosition::status)
            .orderByDesc(UserPosition::id)

        return R.success(userPositionService.list(wrapper))
    }

    @Operation(
        summary = "更新持仓锁仓状态",
        description = "修改持仓的锁仓状态。注意：状态为3（已平仓）或4（平仓失败）的持仓不能修改锁仓状态"
    )
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ApiResponse(responseCode = "400", description = "更新失败：持仓不存在或状态不允许修改")
    @PostMapping("/updateLockStatus")
    fun updateLockStatus(@RequestBody @Validated req: UpdateLockStatusRequest): R<Unit> {
        val position = userPositionService.getById(req.positionId)
            ?: throw BusinessException("持仓不存在")

        // 检查状态：status = 3（已平仓）或 4（平仓失败）不能修改
        if (position.status == "3" || position.status == "4") {
            throw BusinessException("已平仓或平仓失败的持仓不能修改锁仓状态")
        }

        position.isLock = req.isLock
        if (!userPositionService.updateById(position)) {
            throw BusinessException("更新锁仓状态失败")
        }

        return R.success()
    }

    @Operation(
        summary = "强制平仓",
        description = "管理员强制平仓指定持仓，调用 UserPositionService.sell 方法执行平仓逻辑"
    )
    @ApiResponse(responseCode = "200", description = "平仓成功")
    @ApiResponse(responseCode = "400", description = "平仓失败")
    @PostMapping("/forceClose")
    fun forceClose(@RequestBody @Validated req: ForceClosePositionRequest): R<Any> {
        // 根据持仓编号查找持仓
        val position = userPositionService.getById(req.id!!)
            ?: throw BusinessException("持仓不存在")

        // 调用 sell 方法执行平仓逻辑
        return userPositionService.sell(
            positionSn = position.positionSn!!,
            userId = position.userId?.toLong() ?: throw BusinessException("用户ID不存在"),
            doType = req.doType ?: 0,
            actionType = req.actionType ?: "force_close"
        )
    }

}