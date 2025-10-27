package com.fund.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import com.alibaba.fastjson.JSON
import com.fund.common.entity.R
import com.fund.modules.sys.menu.AddMenuReq
import com.fund.modules.sys.menu.UpdateMenuReq
import com.fund.modules.sys.service.SysMenuService
import com.fund.modules.sys.service.SysOptLogService
import com.fund.modules.sys.vo.UserPermissionsVO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

import org.springframework.web.bind.annotation.*

@Tag(name = "菜单管理", description = "菜单权限管理相关接口")
@RestController
@RequestMapping("/manage/sys/menu")
class SysMenuController(
    private val sysMenuService: SysMenuService,
    private val optLogService: SysOptLogService
) {

    @Operation(
        summary = "获取登录用户菜单列表",
        description = "获取当前登录管理员所属的菜单列表"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping(value = ["/menuForLogin"])
    fun getUserPermissions(): R<List<UserPermissionsVO>> {
        val adminId = StpUtil.getLoginIdAsLong()
        return R.success(sysMenuService.menuForLogin(adminId))
    }

    @Operation(
        summary = "获取菜单树状列表",
        description = "获取用于编辑的菜单树状列表"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping(value = ["/menuForEdit"])
    fun getUserPermissions2(): R<List<UserPermissionsVO>> {
        val adminId = StpUtil.getLoginIdAsLong()
        return R.success(sysMenuService.menuForEdit(adminId))
    }

    @Operation(
        summary = "新增菜单",
        description = "新增系统菜单，需要管理员权限"
    )
    @ApiResponse(responseCode = "200", description = "新增成功")
    @PostMapping(value = ["/addMenu"])
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun add(@RequestBody req: AddMenuReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysMenuService.add(adminId, req)
        optLogService.addLog(adminId, "新增菜单", JSON.toJSONString(req))
        return R.success()
    }

    @Operation(
        summary = "更新菜单信息",
        description = "根据菜单ID更新菜单信息，需要管理员权限"
    )
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PostMapping(value = ["/updateById"])
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun updateMenuById(@RequestBody req: UpdateMenuReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysMenuService.updateMenuById(adminId, req)
        optLogService.addLog(adminId, "根据菜单ID更新菜单信息", JSON.toJSONString(req))
        return R.success()
    }

    @Operation(
        summary = "删除菜单",
        description = "根据菜单ID删除菜单信息，需要管理员权限"
    )
    @ApiResponse(responseCode = "200", description = "删除成功")
    @GetMapping(value = ["/deleteById"])
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun deleteById(@Parameter(description = "菜单ID", required = true) @RequestParam("id") id: Long): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysMenuService.deleteById(adminId, id)
        optLogService.addLog(adminId, "根据菜单ID更新菜单信息", id.toString())
        return R.success()
    }
}
