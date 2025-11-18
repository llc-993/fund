package com.fund.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.entity.R
import com.fund.modules.sys.menu.DeleteUserRoleReq
import com.fund.modules.sys.menu.SetUserRoleReq
import com.fund.modules.sys.model.SysUser
import com.fund.modules.sys.service.SysUserService
import com.fund.modules.sys.vo.SysUserRoleVO
import com.fund.utils.DTOUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import kotlin.streams.toList

@Tag(name = "用户角色管理", description = "用户角色关联管理相关接口，包括用户列表查询、角色用户查询、用户角色配置等")
@RestController
@RequestMapping("/sys/menu")
class SysUserController(
    private val sysUserService: SysUserService
    ){

    @GetMapping(value = ["/getUserList"])
    @Operation(
        summary = "查询用户列表",
        description = "获取系统用户列表，用于角色添加用户时选择用户。支持按用户名精确查询，排除超级管理员（ID=1）"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    fun getUserList(
        @Parameter(description = "用户名（精确匹配）", example = "admin")
        @RequestParam(value = "userName", required = false) userName: String?
    ): R<List<SysUser>> {
        val userList = userName?.let {
            sysUserService.list(
                KtQueryWrapper(SysUser())
                    .eq(SysUser::username, it)
            )
        } ?: sysUserService.list()
            .stream()
            .filter{it.id != 1L}
            .toList()

        return R.success(userList)
    }

    @GetMapping(value = ["/getRoleUserList"])
    @Operation(
        summary = "查询角色下的用户列表",
        description = "根据角色ID查询该角色下的所有用户列表"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    fun getRoleUserList(
        @Parameter(description = "角色ID", required = true, example = "1")
        @RequestParam(value = "roleId") roleId: Long
    ): R<List<SysUserRoleVO>> {
        val userList = sysUserService.list()
            .stream()
            .filter { it.getRoleIdList().contains(roleId) }
            .toList()
        return R.success(DTOUtil.toDTO(userList, SysUserRoleVO::class.java))
    }

    @PostMapping(value = ["/setUserRole"])
    @Operation(
        summary = "设置用户角色",
        description = "为指定的用户添加角色，需要 root 角色权限"
    )
    @ApiResponse(responseCode = "200", description = "设置成功")
    @SaCheckOr(role = [SaCheckRole("root")])
    fun setUserRole(@RequestBody @Validated req: SetUserRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysUserService.setUserRole(adminId, req)
        return R.success()
    }

    @PostMapping(value = ["/deleteUserRole"])
    @Operation(
        summary = "删除用户角色",
        description = "从指定的用户中移除角色，需要 root 角色权限"
    )
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SaCheckOr(role = [SaCheckRole("root")])
    fun deleteUserRole(@RequestBody @Validated req: DeleteUserRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysUserService.deleteUserRole(adminId, req)
        return R.success()
    }
}
