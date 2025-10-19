package com.fund.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.sys.model.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@Mapper
interface SysUserMapper : BaseMapper<SysUser> {

}
