package com.fund.modules.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.platform.model.AppPayPlatformUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 支付渠道用户绑定 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-12-27
 */
@Mapper
interface AppPayPlatformUserMapper : BaseMapper<AppPayPlatformUser> {

}
