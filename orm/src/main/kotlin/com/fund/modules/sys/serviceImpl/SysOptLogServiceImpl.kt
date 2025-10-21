package com.fund.modules.sys.serviceImpl;

import com.fund.modules.sys.model.SysOptLog;
import com.fund.modules.sys.mapper.SysOptLogMapper;
import com.fund.modules.sys.service.SysOptLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.modules.sys.mapper.SysUserMapper
import com.fund.utils.IpUtils
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
open class SysOptLogServiceImpl(
    private val sysUserMapper: SysUserMapper
) : ServiceImpl<SysOptLogMapper, SysOptLog>(),
    SysOptLogService {

    override fun addLog(adminId: Long, remark: String, json: String) {
        val log = SysOptLog()
        val admin = sysUserMapper.selectById(adminId)
        log.optUser = admin?.username
        log.ip = IpUtils.getIpAddr()
        log.remark = remark
        log.json = json
        save(log)
    }
}
