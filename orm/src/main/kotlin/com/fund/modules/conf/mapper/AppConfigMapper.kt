package com.fund.modules.conf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.conf.model.AppConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * app配置 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@Mapper
interface AppConfigMapper : BaseMapper<AppConfig> {

}
