package com.fund.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.user.model.AppUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@Mapper
interface AppUserMapper : BaseMapper<AppUser> {

}
