package com.fund.modules.ipo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.ipo.model.Ipo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * IPO信息表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-10-07
 */
@Mapper
interface IpoMapper : BaseMapper<Ipo> {

}
