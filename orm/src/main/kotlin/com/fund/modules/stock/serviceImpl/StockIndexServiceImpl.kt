package com.fund.modules.stock.serviceImpl;

import com.fund.modules.stock.model.StockIndex;
import com.fund.modules.stock.mapper.StockIndexMapper;
import com.fund.modules.stock.service.StockIndexService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-12-28
 */
@Service
open class StockIndexServiceImpl : ServiceImpl<StockIndexMapper, StockIndex>(), StockIndexService {

}
