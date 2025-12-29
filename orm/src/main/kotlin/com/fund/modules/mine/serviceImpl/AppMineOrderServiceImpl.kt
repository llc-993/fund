package com.fund.modules.mine.serviceImpl;

import com.fund.modules.mine.model.AppMineOrder;
import com.fund.modules.mine.mapper.AppMineOrderMapper;
import com.fund.modules.mine.service.AppMineOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 锁仓挖矿项目订单 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-12-29
 */
@Service
open class AppMineOrderServiceImpl : ServiceImpl<AppMineOrderMapper, AppMineOrder>(), AppMineOrderService {

}
