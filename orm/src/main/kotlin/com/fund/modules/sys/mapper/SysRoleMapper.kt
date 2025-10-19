package com.fund.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.sys.model.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 后台系统角色 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@Mapper
interface SysRoleMapper : BaseMapper<SysRole> {

}
