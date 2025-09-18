package com.fund.modules.agent.serviceImpl;

import com.fund.modules.agent.model.AppAgentMoveLog;
import com.fund.modules.agent.mapper.AppAgentMoveLogMapper;
import com.fund.modules.agent.service.AppAgentMoveLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 代理迁移记录 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@Service
open class AppAgentMoveLogServiceImpl : ServiceImpl<AppAgentMoveLogMapper, AppAgentMoveLog>(), AppAgentMoveLogService {

}
