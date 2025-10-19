package com.fund.modules.sys.serviceImpl;

import com.fund.modules.sys.model.SysJob;
import com.fund.modules.sys.mapper.SysJobMapper;
import com.fund.modules.sys.service.SysJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定时任务 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@Service
open class SysJobServiceImpl : ServiceImpl<SysJobMapper, SysJob>(), SysJobService {

}
