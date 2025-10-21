package com.fund.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import com.alibaba.fastjson.JSON
import com.fund.common.entity.R
import com.fund.modules.sys.service.SysMenuService
import com.fund.modules.sys.service.SysOptLogService
import org.springframework.web.bind.annotation.*


/**
 * manage-菜单权限管理
 */
@RestController
@RequestMapping("/manage/sys/menu")
class SysMenuController(
    private val sysMenuService: SysMenuService,
    private val optLogService: SysOptLogService
) {

    @GetMapping(value = ["/menuForLogin"])
    @ApiOperation("获取管理员登陆所属菜单列表")
    fun getUserPermissions(): R<List<UserPermissionsVO>> {
        val adminId = StpUtil.getLoginIdAsLong()
        return R.success(sysMenuService.menuForLogin(adminId))
    }

    @GetMapping(value = ["/menuForEdit"])
    @ApiOperation("获取菜单树状列表")
    fun getUserPermissions2(): R<List<UserPermissionsVO>> {
        val adminId = StpUtil.getLoginIdAsLong()
        return R.success(sysMenuService.menuForEdit(adminId))
    }

    @PostMapping(value = ["/addMenu"])
    @ApiOperation("新增菜单")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun add(@RequestBody req: AddMenuReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysMenuService.add(adminId, req)
        optLogService.addLog(adminId, "新增菜单", JSON.toJSONString(req))
        return R.success()
    }

    @PostMapping(value = ["/updateById"])
    @ApiOperation("根据菜单ID更新菜单信息")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun updateMenuById(@RequestBody req: UpdateMenuReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysMenuService.updateMenuById(adminId, req)
        optLogService.addLog(adminId, "根据菜单ID更新菜单信息", JSON.toJSONString(req))
        return R.success()
    }

    @GetMapping(value = ["/deleteById"])
    @ApiOperation("根据菜单ID删除菜单信息")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun deleteById(@ApiParam(value = "菜单ID") @RequestParam("id") id: Long): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysMenuService.deleteById(adminId, id)
        optLogService.addLog(adminId, "根据菜单ID更新菜单信息", id.toString())
        return R.success()
    }
}
