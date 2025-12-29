package com.fund.modules.mine.serviceImpl;

import com.fund.modules.mine.model.AppMineProject;
import com.fund.modules.mine.mapper.AppMineProjectMapper;
import com.fund.modules.mine.service.AppMineProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 锁仓挖矿项目 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-12-29
 */
@Service
open class AppMineProjectServiceImpl : ServiceImpl<AppMineProjectMapper, AppMineProject>(), AppMineProjectService {

}
