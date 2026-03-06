package com.fund.controller.quotation

import cn.dev33.satoken.stp.StpUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.quotation.DeleteQuotationControlRequest
import com.fund.modules.quotation.QueryQuotationControlRequest
import com.fund.modules.quotation.SetQuotationControlRequest
import com.fund.modules.quotation.model.UserQuotationControl
import com.fund.modules.quotation.service.UserQuotationControlService
import com.fund.modules.sys.service.SysOptLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 用户行情调控管理接口
 */
@Tag(name = "用户行情调控", description = "管理员调整指定用户的行情价格")
@RestController
@RequestMapping("/quotation/control")
class UserQuotationControlController(
    private val controlService: UserQuotationControlService,
    private val optLogService: SysOptLogService
) {

    @Operation(summary = "设置用户行情调控", description = "设置指定用户的价格浮动值")
    @PostMapping("/set")
    fun setControl(@RequestBody @Validated req: SetQuotationControlRequest): R<Unit> {
        val result = controlService.setControl(
            userId = req.userId,
            symbol = req.symbol,
            stockType = req.stockType,
            floating = req.floating,
            effectTime = req.effectTime,
            remark = req.remark
        )
        if (!result) {
            throw BusinessException("设置失败")
        }
        optLogService.addLog(StpUtil.getLoginIdAsLong(), "设置用户行情调控", JSON.toJSONString(req))
        return R.success()
    }

    @Operation(summary = "查询用户行情调控列表")
    @PostMapping("/list")
    fun list(@RequestBody req: QueryQuotationControlRequest): R<List<UserQuotationControl>> {
        val wrapper = KtQueryWrapper(UserQuotationControl())
            .eq(req.userId != null, UserQuotationControl::userId, req.userId)
            .eq(!req.symbol.isNullOrBlank(), UserQuotationControl::symbol, req.symbol)
            .eq(!req.stockType.isNullOrBlank(), UserQuotationControl::stockType, req.stockType)
            .eq(req.isActive != null, UserQuotationControl::isActive, req.isActive)
            .orderByDesc(UserQuotationControl::id)
        return R.success(controlService.list(wrapper))
    }

    @Operation(summary = "删除用户行情调控")
    @PostMapping("/delete")
    fun delete(@RequestBody @Validated req: DeleteQuotationControlRequest): R<Unit> {
        val result = controlService.removeById(req.id)
        if (!result) {
            throw BusinessException("删除失败")
        }
        optLogService.addLog(StpUtil.getLoginIdAsLong(), "删除用户行情调控", JSON.toJSONString(req))
        return R.success()
    }

    @Operation(summary = "启用/禁用用户行情调控")
    @PostMapping("/toggle")
    fun toggle(@RequestBody req: DeleteQuotationControlRequest): R<Unit> {
        val control = controlService.getById(req.id) ?: throw BusinessException("记录不存在")
        control.isActive = if (control.isActive == 1.toByte()) 0 else 1
        controlService.updateById(control)
        optLogService.addLog(StpUtil.getLoginIdAsLong(), "切换用户行情调控状态", JSON.toJSONString(req))
        return R.success()
    }
}
