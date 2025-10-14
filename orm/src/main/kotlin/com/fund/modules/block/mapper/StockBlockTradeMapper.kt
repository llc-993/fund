package com.fund.modules.block.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.block.model.StockBlockTrade;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 股票大宗交易 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-10-10
 */
@Mapper
interface StockBlockTradeMapper : BaseMapper<StockBlockTrade> {

}
