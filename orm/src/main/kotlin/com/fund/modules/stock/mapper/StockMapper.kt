package com.fund.modules.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.stock.model.Stock;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 股票行情数据表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-08-12
 */
@Mapper
interface StockMapper : BaseMapper<Stock> {

}
