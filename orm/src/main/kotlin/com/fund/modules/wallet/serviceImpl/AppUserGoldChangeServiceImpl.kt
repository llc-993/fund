package com.fund.modules.wallet.serviceImpl;

import com.fund.modules.wallet.model.AppUserGoldChange;
import com.fund.modules.wallet.mapper.AppUserGoldChangeMapper;
import com.fund.modules.wallet.service.AppUserGoldChangeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员账变记录表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
@Service
open class AppUserGoldChangeServiceImpl : ServiceImpl<AppUserGoldChangeMapper, AppUserGoldChange>(), AppUserGoldChangeService {

}
