package com.fund.modules.sys.serviceImpl;

import com.fund.modules.sys.model.SysMenu;
import com.fund.modules.sys.mapper.SysMenuMapper;
import com.fund.modules.sys.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@Service
open class SysMenuServiceImpl : ServiceImpl<SysMenuMapper, SysMenu>(), SysMenuService {

}
