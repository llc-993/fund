package com.fund.modules.doc.serviceImpl;

import com.fund.modules.doc.model.AppDoc;
import com.fund.modules.doc.mapper.AppDocMapper;
import com.fund.modules.doc.service.AppDocService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP文案 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-11-14
 */
@Service
open class AppDocServiceImpl : ServiceImpl<AppDocMapper, AppDoc>(), AppDocService {

}
