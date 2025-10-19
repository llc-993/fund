package com.fund.modules.sys.serviceImpl;

import com.fund.modules.sys.model.SysRole;
import com.fund.modules.sys.mapper.SysRoleMapper;
import com.fund.modules.sys.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
