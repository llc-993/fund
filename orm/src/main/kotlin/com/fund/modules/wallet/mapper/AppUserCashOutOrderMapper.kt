package com.fund.modules.wallet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.wallet.model.AppUserCashOutOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户提现订单表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-10-18
 */
@Mapper
interface AppUserCashOutOrderMapper : BaseMapper<AppUserCashOutOrder> {

}
