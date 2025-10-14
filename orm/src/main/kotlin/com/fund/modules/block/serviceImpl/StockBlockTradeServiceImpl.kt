package com.fund.modules.block.serviceImpl;

import com.fund.modules.block.model.StockBlockTrade;
import com.fund.modules.block.mapper.StockBlockTradeMapper;
import com.fund.modules.block.service.StockBlockTradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 股票大宗交易 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-10
 */
@Service
open class StockBlockTradeServiceImpl : ServiceImpl<StockBlockTradeMapper, StockBlockTrade>(), StockBlockTradeService {

}
