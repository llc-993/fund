package com.fund.modules.sys.service;

import com.fund.modules.sys.model.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.modules.sys.menu.DeleteUserRoleReq
import com.fund.modules.sys.menu.SetUserRoleReq

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
interface SysUserService : IService<SysUser> {
    
    /**
     * 设置用户角色
     */
    fun setUserRole(adminId: Long, req: SetUserRoleReq)
    
    /**
     * 删除用户角色
     */
    fun deleteUserRole(adminId: Long, req: DeleteUserRoleReq)
}
