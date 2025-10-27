package com.fund.modules.sys.serviceImpl;

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.fund.modules.sys.mapper.SysUserMapper
import com.fund.modules.sys.menu.DeleteUserRoleReq
import com.fund.modules.sys.menu.SetUserRoleReq
import com.fund.modules.sys.model.SysUser
import com.fund.modules.sys.service.SysUserService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@Service
open class SysUserServiceImpl : ServiceImpl<SysUserMapper, SysUser>(), SysUserService {

    /**
     * 设置用户角色
     * 为指定的用户添加角色
     * 
     * @param adminId 管理员ID
     * @param req 设置用户角色请求参数，包含角色ID和用户ID列表
     */
    override fun setUserRole(adminId: Long, req: SetUserRoleReq) {
        // 为多个用户设置角色
        req.userIds?.forEach { userId ->
            val user = getById(userId) ?: return@forEach
            val currentRoleIds = user.getRoleIdList().toMutableList()
            // 添加新角色ID（如果不存在）
            if (req.roleId != null && !currentRoleIds.contains(req.roleId)) {
                currentRoleIds.add(req.roleId!!)
                user.roleIds = currentRoleIds.joinToString(",")
                updateById(user)
            }
        }
    }

    /**
     * 删除用户角色
     * 从指定的用户中移除角色
     * 
     * @param adminId 管理员ID
     * @param req 删除用户角色请求参数，包含角色ID和用户ID列表
     */
    override fun deleteUserRole(adminId: Long, req: DeleteUserRoleReq) {
        // 从多个用户中删除角色
        req.userIds?.forEach { userId ->
            val user = getById(userId) ?: return@forEach
            val currentRoleIds = user.getRoleIdList().toMutableList()
            // 移除角色ID
            if (req.roleId != null) {
                currentRoleIds.remove(req.roleId)
                user.roleIds = currentRoleIds.joinToString(",")
                updateById(user)
            }
        }
    }
}
