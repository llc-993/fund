package com.fund.modules.sys.serviceImpl;

import cn.hutool.core.collection.CollUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.modules.sys.model.SysMenu;
import com.fund.modules.sys.mapper.SysMenuMapper;
import com.fund.modules.sys.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.exception.BusinessException
import com.fund.modules.sys.mapper.SysRoleMapper
import com.fund.modules.sys.mapper.SysUserMapper
import com.fund.modules.sys.menu.AddMenuReq
import com.fund.modules.sys.menu.UpdateMenuReq
import com.fund.modules.sys.model.SysRole
import com.fund.modules.sys.vo.UserPermissionsVO
import com.fund.utils.DTOUtil
import org.springframework.stereotype.Service;
import java.time.LocalDateTime
import java.util.Date

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@Service
open class SysMenuServiceImpl(
    private val sysUserMapper: SysUserMapper,
    private val sysMenuMapper: SysMenuMapper,
    private val sysRoleMapper: SysRoleMapper
): ServiceImpl<SysMenuMapper, SysMenu>(), SysMenuService {

    /**
     * 构建树节点配置
     * 配置节点ID键、父节点ID键、根节点条件和排序比较器
     * 
     * @return 节点配置对象
     */
    private fun buildNodeConfig(): DTOUtil.NodeConfig<UserPermissionsVO> {
        val nodeConfig: DTOUtil.NodeConfig<UserPermissionsVO> = DTOUtil.NodeConfig()
        nodeConfig.idKey = "menuId"
        nodeConfig.pidKey = "parentId"
        nodeConfig.rootCondition = 0L

        nodeConfig.comparator = Comparator<UserPermissionsVO> { o1, o2 ->
            val v1 = o1?.orderNum ?: 0
            val v2 = o2?.orderNum ?: 0
            v1.compareTo(v2)
        }
        return nodeConfig
    }

    /**
     * 获取登录用户菜单列表
     * 根据管理员ID获取其可访问的菜单树状结构
     * 
     * @param adminId 管理员ID
     * @return 菜单树状列表
     */
    override fun menuForLogin(adminId: Long): List<UserPermissionsVO> {
        val isRoot = adminId == 1L
        val menuList = if (isRoot) findAllMenuList() else findUserMenuList(adminId, true)

        return DTOUtil.toNodeTree(menuList, UserPermissionsVO::class.java, { menu ->
            menu.meta.icon = menu.icon
            menu.meta.title = menu.menuName
        }, buildNodeConfig())!!
    }

    /**
     * 获取用于编辑的菜单树状列表
     * 返回所有菜单用于编辑页面显示
     * 
     * @param adminId 管理员ID
     * @return 菜单树状列表
     */
    override fun menuForEdit(adminId: Long): List<UserPermissionsVO> {
        val isRoot = adminId == 1L

        // 1:启用 0:禁用
        val menuList = if (isRoot) findAllMenuList() else findUserMenuList(adminId, null)

        return DTOUtil.toNodeTree(menuList, UserPermissionsVO::class.java, { menu ->
            menu.meta.icon = menu.icon
            menu.meta.title = menu.menuName
        }, buildNodeConfig())!!
    }

    /**
     * 查找用户菜单列表
     * 根据用户ID和状态查询用户有权限访问的菜单
     * 
     * @param userId 用户ID
     * @param status 菜单状态（true=启用，null=所有）
     * @return 菜单列表
     */
    private fun findUserMenuList(userId: Long, status: Boolean?): List<SysMenu> {
        val sysUser = sysUserMapper.selectById(userId) ?: return emptyList()

        val roleIds: List<Long> = sysUser.getRoleIdList()

        val menuIdList:MutableList<Long> = mutableListOf()
        val roleList = sysRoleMapper.selectList(
            KtQueryWrapper(SysRole())
                .`in`(SysRole::roleId, roleIds)
        )
        for (role in roleList) {
            menuIdList.addAll(role.getMenuIdList())
        }

        if (CollUtil.isEmpty(menuIdList)) {
            return emptyList()
        }

        return baseMapper.selectList(
            KtQueryWrapper(SysMenu())
                // 1:启用 0:禁用
                .eq(status != null, SysMenu::status, status)
                .`in`(SysMenu::menuId, menuIdList)
        )
    }

    /**
     * 查找所有启用的菜单列表
     * 
     * @return 菜单列表
     */
    private fun findAllMenuList() = super<ServiceImpl>.list(
        KtQueryWrapper(SysMenu())
            .eq(SysMenu::status, true)
    )

    /**
     * 更新菜单信息
     * 根据管理员ID和更新请求更新菜单
     * 
     * @param adminId 管理员ID
     * @param req 更新菜单请求参数
     */
    override fun updateMenuById(adminId: Long, req: UpdateMenuReq) {
        val menu: SysMenu? = DTOUtil.toDTO(req, SysMenu::class.java)
        menu ?: throw BusinessException("参数转化异常")
        val admin = sysUserMapper.selectById(adminId) ?: throw BusinessException("管理员不存在")
        menu.updateBy = admin.username
        menu.updateTime = LocalDateTime.now()

        baseMapper.updateById(menu)
    }

    /**
     * 删除菜单
     * 根据菜单ID删除菜单
     * 
     * @param adminId 管理员ID
     * @param id 菜单ID
     */
    override fun deleteById(adminId: Long, id: Long) {
        //删除菜单
        baseMapper.deleteById(id)
    }

    /**
     * 新增菜单
     * 根据管理员ID和新增请求创建新菜单
     * 
     * @param adminId 管理员ID
     * @param req 新增菜单请求参数
     */
    override fun add(adminId: Long, req: AddMenuReq) {
        val menu: SysMenu? = DTOUtil.toDTO(req, SysMenu::class.java)
        menu ?: throw BusinessException("参数转化异常")
        val admin = sysUserMapper.selectById(adminId) ?: throw BusinessException("管理员不存在")
        menu.createBy = admin.username
        menu.updateTime = LocalDateTime.now()
        if (menu.parentId == 0L) {
            menu.component = "Layout"
        }
        sysMenuMapper.insert(menu)
    }

}
