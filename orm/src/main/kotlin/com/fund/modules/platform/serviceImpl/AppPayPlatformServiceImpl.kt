package com.fund.modules.platform.serviceImpl;

import com.fund.modules.platform.model.AppPayPlatform;
import com.fund.modules.platform.mapper.AppPayPlatformMapper;
import com.fund.modules.platform.service.AppPayPlatformService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付平台配置 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-12-27
 */
@Service
open class AppPayPlatformServiceImpl : ServiceImpl<AppPayPlatformMapper, AppPayPlatform>(), AppPayPlatformService {

}
