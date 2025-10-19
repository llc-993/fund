package com.fund.modules.sys.serviceImpl;

import com.fund.modules.sys.model.SysOptLog;
import com.fund.modules.sys.mapper.SysOptLogMapper;
import com.fund.modules.sys.service.SysOptLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@Service
open class SysOptLogServiceImpl : ServiceImpl<SysOptLogMapper, SysOptLog>(), SysOptLogService {

}
