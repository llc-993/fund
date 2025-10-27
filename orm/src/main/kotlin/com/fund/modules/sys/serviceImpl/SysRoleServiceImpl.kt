package com.fund.modules.sys.serviceImpl;

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.exception.BusinessException
import com.fund.modules.sys.mapper.SysRoleMapper
import com.fund.modules.sys.menu.AddRoleReq
import com.fund.modules.sys.menu.SetRoleMenuReq
import com.fund.modules.sys.menu.UpdateRoleReq
import com.fund.modules.sys.model.SysRole
import com.fund.modules.sys.service.SysRoleService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service

/**
 * <p>
 * 后台系统角色 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@Service
open class SysRoleServiceImpl : ServiceImpl<SysRoleMapper, SysRole>(), SysRoleService {

    /**
     * 获取角色列表
     * 
     * @param roleName 角色名称，支持模糊查询
     * @return 角色列表
     */
    override fun getRoleList(roleName: String?): List<SysRole> {
        return list(
            KtQueryWrapper(SysRole())
                .like(StrUtil.isNotBlank(roleName), SysRole::roleName, roleName)
        )
    }

    /**
     * 新增角色
     * 
     * @param adminId 管理员ID
     * @param req 新增角色请求参数
     */
    override fun addRole(adminId: Long, req: AddRoleReq) {
        val role = SysRole()
        role.roleName = req.roleName
        role.roleCode = req.roleCode
        role.roleStatus = req.roleStatus
        this.save(role)
    }

    /**
     * 更新角色
     * 
     * @param adminId 管理员ID
     * @param req 更新角色请求参数
     */
    override fun updateRole(adminId: Long, req: UpdateRoleReq) {
        val role = getById(req.roleId) ?: return
        req.roleName?.let { role.roleName = it }
        req.roleStatus?.let { role.roleStatus = it }
        this.updateById(role)
    }

    /**
     * 删除角色
     * 
     * @param adminId 管理员ID
     * @param roleIds 要删除的角色ID列表
     */
    override fun deleteRole(adminId: Long, roleIds: List<Long>) {
        this.removeByIds(roleIds)
    }

    /**
     * 设置角色菜单权限
     * 
     * @param adminId 管理员ID
     * @param req 设置角色菜单请求参数，包含角色ID和菜单ID列表
     */
    override fun setRoleMenu(adminId: Long, req: SetRoleMenuReq) {
        val role = getById(req.roleId) ?: throw BusinessException("角色不存在")
        val menuIdStr = if (CollUtil.isNotEmpty(req.menuIds)) {
            req.menuIds?.joinToString(",") ?: ""
        } else {
            ""
        }
        role.menuIds = menuIdStr
        this.updateById(role)
    }
}
