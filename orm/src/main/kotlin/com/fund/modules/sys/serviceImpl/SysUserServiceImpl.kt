package com.fund.modules.sys.serviceImpl;

import com.fund.modules.sys.model.SysUser;
import com.fund.modules.sys.mapper.SysUserMapper;
import com.fund.modules.sys.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
