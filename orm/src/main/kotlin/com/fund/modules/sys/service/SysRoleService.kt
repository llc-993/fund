package com.fund.modules.sys.service;

import com.fund.modules.sys.model.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.modules.sys.menu.AddRoleReq
import com.fund.modules.sys.menu.SetRoleMenuReq
import com.fund.modules.sys.menu.UpdateRoleReq

/**
 * <p>
 * 后台系统角色 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
interface SysRoleService : IService<SysRole> {
    
    /**
     * 获取角色列表
     */
    fun getRoleList(roleName: String?): List<SysRole>
    
    /**
     * 新增角色
     */
    fun addRole(adminId: Long, req: AddRoleReq)
    
    /**
     * 更新角色
     */
    fun updateRole(adminId: Long, req: UpdateRoleReq)
    
    /**
     * 删除角色
     */
    fun deleteRole(adminId: Long, roleIds: List<Long>)
    
    /**
     * 设置角色菜单
     */
    fun setRoleMenu(adminId: Long, req: SetRoleMenuReq)
}
