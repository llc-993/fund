package com.fund.modules.news.serviceImpl;

import com.fund.modules.news.model.StockNews;
import com.fund.modules.news.mapper.StockNewsMapper;
import com.fund.modules.news.service.StockNewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 股票新闻表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-11-28
 */
@Service
open class StockNewsServiceImpl : ServiceImpl<StockNewsMapper, StockNews>(), StockNewsService {

}
