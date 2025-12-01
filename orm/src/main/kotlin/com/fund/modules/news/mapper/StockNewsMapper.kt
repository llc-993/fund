package com.fund.modules.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.news.model.StockNews;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 股票新闻表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-11-28
 */
@Mapper
interface StockNewsMapper : BaseMapper<StockNews> {

}
