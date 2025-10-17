package com.fund.modules.risingFalling.serviceImpl;

import com.fund.modules.risingFalling.model.RisingFallingSectors;
import com.fund.modules.risingFalling.mapper.RisingFallingSectorsMapper;
import com.fund.modules.risingFalling.service.RisingFallingSectorsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 涨跌板块 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-17
 */
@Service
open class RisingFallingSectorsServiceImpl : ServiceImpl<RisingFallingSectorsMapper, RisingFallingSectors>(), RisingFallingSectorsService {

}
