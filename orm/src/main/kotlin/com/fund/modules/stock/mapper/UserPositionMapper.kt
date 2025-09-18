package com.fund.modules.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.stock.model.UserPosition;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户持仓表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
@Mapper
interface UserPositionMapper : BaseMapper<UserPosition> {

}
