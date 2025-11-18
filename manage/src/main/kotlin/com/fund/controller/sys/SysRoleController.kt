package com.fund.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.sys.menu.AddRoleReq
import com.fund.modules.sys.menu.DeleteRoleReq
import com.fund.modules.sys.menu.SetRoleMenuReq
import com.fund.modules.sys.menu.UpdateRoleReq
import com.fund.modules.sys.model.SysRole
import com.fund.modules.sys.service.SysRoleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "角色管理", description = "系统角色管理相关接口，包括角色列表查询、新增、更新、删除以及角色菜单权限配置")
@RestController
@RequestMapping("/sys/menu")
class SysRoleController(
    private val sysRoleService: SysRoleService
    ){
    
    @GetMapping(value = ["/getRoleMenuByRoleId"])
    @Operation(
        summary = "根据角色ID查询菜单ID列表",
        description = "根据角色ID查询该角色拥有的所有菜单ID集合，用于角色权限管理"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    fun getRoleMenuByRoleId(
        @Parameter(description = "角色ID", required = true, example = "1")
        @RequestParam("roleId") roleId: Long
    ): R<List<Long>> {
        val role = sysRoleService.getById(roleId) ?: throw BusinessException("找不到角色")
        return R.success(role.getMenuIdList())
    }

    @PostMapping(value = ["/setRoleMenu"])
    @Operation(
        summary = "设置角色菜单权限",
        description = "为指定角色配置菜单权限，包括菜单的增删改查等操作权限。需要 root 角色权限"
    )
    @ApiResponse(responseCode = "200", description = "设置成功")
    @SaCheckOr(role = [SaCheckRole("root")])
    fun setRoleMenu(@RequestBody @Validated req: SetRoleMenuReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysRoleService.setRoleMenu(adminId, req)
        return R.success()
    }

    @GetMapping(value = ["/getRoleList"])
    @Operation(
        summary = "查询角色列表",
        description = "获取系统所有角色列表，支持按角色名称模糊查询"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    fun getRoleList(
        @Parameter(description = "角色名称（支持模糊查询）", example = "管理员")
        @RequestParam(value = "roleName", required = false) roleName: String?
    ): R<List<SysRole>> {
        return R.success(sysRoleService.getRoleList(roleName))
    }

    @PostMapping(value = ["/addRole"])
    @Operation(
        summary = "新增角色",
        description = "创建新的系统角色，需要 root 角色权限"
    )
    @ApiResponse(responseCode = "200", description = "新增成功")
    @SaCheckOr(role = [SaCheckRole("root")])
    fun addRole(@RequestBody @Validated req: AddRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysRoleService.addRole(adminId, req)
        return R.success()
    }

    @PostMapping(value = ["/updateRole"])
    @Operation(
        summary = "更新角色信息",
        description = "更新现有角色的信息，包括角色名称、状态等。需要 root 角色权限"
    )
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SaCheckOr(role = [SaCheckRole("root")])
    fun updateRole(@RequestBody @Validated req: UpdateRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysRoleService.updateRole(adminId, req)
        return R.success()
    }

    @PostMapping(value = ["/deleteRole"])
    @Operation(
        summary = "批量删除角色",
        description = "根据角色ID列表批量删除系统角色，需要 root 角色权限"
    )
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SaCheckOr(role = [SaCheckRole("root")])
    fun deleteRole(@RequestBody req: DeleteRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysRoleService.deleteRole(adminId, req.roleIds ?: emptyList())
        return R.success()
    }
}
