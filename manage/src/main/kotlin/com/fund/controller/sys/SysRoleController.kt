package org.lemon.api.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.lemon.api.common.domain.R
import org.lemon.api.common.exception.BusinessException
import org.lemon.api.modules.auth.dto.AdminLoginUser
import org.lemon.api.modules.sys.domain.co.AddRoleReq
import org.lemon.api.modules.sys.domain.co.DeleteRoleReq
import org.lemon.api.modules.sys.domain.co.SetRoleMenuReq
import org.lemon.api.modules.sys.domain.co.UpdateRoleReq
import org.lemon.api.modules.sys.domain.entity.SysRole
import org.lemon.api.modules.sys.service.SysRoleService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Api(tags = ["manage-菜单权限管理-角色"])
@RestController
@RequestMapping("/manage/sys/menu")
class SysRoleController(
    private val sysRoleService: SysRoleService
    ){
    /************************************************角色配置菜单关系 */
    @GetMapping(value = ["/getRoleMenuByRoleId"])
    @ApiOperation("根据角色ID查询对应拥有的角色菜单id集合")
    fun getRoleMenuByRoleId(@ApiParam(value = "角色ID") @RequestParam("roleId") roleId: Long): R<List<Long>> {
        val role = sysRoleService.getById(roleId) ?: throw BusinessException("找不到角色")
        return R.success(role.getMenuIdList())
    }

    @PostMapping(value = ["/setRoleMenu"])
    @ApiOperation("设置角色菜单配置")
    @SaCheckOr(
        role = [SaCheckRole("root")]
    )
    fun setRoleMenu(@RequestBody @Validated req: SetRoleMenuReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysRoleService.setRoleMenu(adminId, req)
        return R.success()
    }

    /***********************************************角色配置 */
    @GetMapping(value = ["/getRoleList"])
    @ApiOperation("查询角色列表")
    fun getRoleList(
        @ApiParam(value = "角色名称") @RequestParam(
            value = "roleName",
            required = false
        ) roleName: String?
    ): R<List<SysRole>> {
        return R.success(sysRoleService.getRoleList(roleName!!))
    }

    @PostMapping(value = ["/addRole"])
    @ApiOperation("新增角色")
    @SaCheckOr(
        role = [SaCheckRole("root")]
    )
    fun addRole(@RequestBody @Validated req: AddRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysRoleService.addRole(adminId, req)
        return R.success()
    }

    @PostMapping(value = ["/updateRole"])
    @ApiOperation("更新角色信息")
    @SaCheckOr(
        role = [SaCheckRole("root")]
    )
    fun updateRole(@RequestBody @Validated req: UpdateRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysRoleService.updateRole(adminId, req)
        return R.success()
    }

    @PostMapping(value = ["/deleteRole"])
    @ApiOperation("批量删除角色信息")
    @SaCheckOr(
        role = [SaCheckRole("root")]
    )
    fun deleteRole(@RequestBody req: DeleteRoleReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        sysRoleService.deleteRole(adminId, req.roleIds!!)
        return R.success()
    }
}
