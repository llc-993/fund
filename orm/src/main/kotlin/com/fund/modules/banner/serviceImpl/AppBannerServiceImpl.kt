package com.fund.modules.banner.serviceImpl;

import com.fund.modules.banner.model.AppBanner;
import com.fund.modules.banner.mapper.AppBannerMapper;
import com.fund.modules.banner.service.AppBannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * app banner 轮播图 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-11-14
 */
@Service
open class AppBannerServiceImpl : ServiceImpl<AppBannerMapper, AppBanner>(), AppBannerService {

}
