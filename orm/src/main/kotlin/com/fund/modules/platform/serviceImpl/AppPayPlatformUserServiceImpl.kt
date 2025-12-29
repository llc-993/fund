package com.fund.modules.platform.serviceImpl;

import com.fund.modules.platform.model.AppPayPlatformUser;
import com.fund.modules.platform.mapper.AppPayPlatformUserMapper;
import com.fund.modules.platform.service.AppPayPlatformUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付渠道用户绑定 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-12-27
 */
@Service
open class AppPayPlatformUserServiceImpl : ServiceImpl<AppPayPlatformUserMapper, AppPayPlatformUser>(), AppPayPlatformUserService {

}
