package com.fund.modules.sys.service;

import com.fund.modules.sys.model.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.modules.sys.menu.AddMenuReq
import com.fund.modules.sys.menu.UpdateMenuReq
import com.fund.modules.sys.vo.UserPermissionsVO

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
interface SysMenuService : IService<SysMenu> {

    fun menuForLogin(adminId: Long): List<UserPermissionsVO>

    fun menuForEdit(adminId: Long): List<UserPermissionsVO>

    fun updateMenuById(adminId: Long, req: UpdateMenuReq)

    fun deleteById(adminId: Long, id: Long)

    fun add(adminId: Long, req: AddMenuReq)

}
