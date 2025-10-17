package com.fund.modules.block.mapper;

import com.fund.modules.block.model.StockBlockTradeSubscription;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper

/**
 * <p>
 * 大宗交易申购记录 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-10-16
 */
@Mapper
interface StockBlockTradeSubscriptionMapper : BaseMapper<StockBlockTradeSubscription>

