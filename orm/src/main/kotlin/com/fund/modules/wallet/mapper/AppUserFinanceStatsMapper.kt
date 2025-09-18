package com.fund.modules.wallet.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.fund.modules.wallet.model.AppUserFinanceStats
import org.apache.ibatis.annotations.Mapper

/**
 * <p>
 * 用户资金统计表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-01-27
 */
@Mapper
interface AppUserFinanceStatsMapper : BaseMapper<AppUserFinanceStats> {

}
