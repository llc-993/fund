package org.lemon.api.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.lemon.api.common.domain.R
import org.lemon.api.common.mybatisplus.DTOUtil
import org.lemon.api.modules.auth.dto.AdminLoginUser
import org.lemon.api.modules.sys.domain.co.*
import org.lemon.api.modules.sys.domain.entity.SysUser
import org.lemon.api.modules.sys.domain.vo.SysUserRoleVO
import org.lemon.api.modules.sys.service.SysUserService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import kotlin.streams.toList

@Api(tags = ["manage-菜单权限管理-用户"])
@RestController
@RequestMapping("/manage/sys/menu")
class SysUserController(
    private val sysUserService: SysUserService
    ){

    /***********************************************用户分配角色 */
    @GetMapping(value = ["/getUserList"])
    @ApiOperation("查询用户列表（此接口只用于角色添加用户查询用户列表使用）")
    fun getUserList(
        @ApiParam(value = "用户名") @RequestParam(value = "userName", required = false) userName: String?
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
    @ApiOperation("查询角色下的用户列表")
    fun getRoleUserList(
        @ApiParam(value = "角色ID") @RequestParam(value = "roleId") roleId: Long
    ): R<List<SysUserRoleVO>> {
        val userList = sysUserService.list()
            .stream()
            .filter { it.getRoleIdList().contains(roleId) }
            .toList()
        return R.success(DTOUtil.toDTO(userList, SysUserRoleVO::class.java))
    }

    @PostMapping(value = ["/setUserRole"])
    @ApiOperation("设置用户角色配置")
    @SaCheckOr(
        role = [SaCheckRole("root")]
    )
    fun setUserRole(@RequestBody @Validated req: SetUserRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysUserService.setUserRole(adminId, req)
        return R.success()
    }

    @PostMapping(value = ["/deleteUserRole"])
    @ApiOperation("批量删除角色配置下的用户")
    @SaCheckOr(
        role = [SaCheckRole("root")]
    )
    fun deleteUserRole(@RequestBody @Validated req: DeleteUserRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysUserService.deleteUserRole(adminId, req)
        return R.success()
    }
}
