package com.fund.modules.notice.serviceImpl;

import com.fund.modules.notice.model.AppNotice;
import com.fund.modules.notice.mapper.AppNoticeMapper;
import com.fund.modules.notice.service.AppNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统公告 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-11-14
 */
@Service
open class AppNoticeServiceImpl : ServiceImpl<AppNoticeMapper, AppNotice>(), AppNoticeService {

}
