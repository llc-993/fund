package com.fund.modules.conf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.conf.model.AppConfig;
import com.fund.modules.sys.vo.HomeData
import com.fund.modules.sys.vo.UndoData
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param
import java.util.Date

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


    fun selectHomeData(
        @Param("topId") topId: Long?,
        @Param("startTime") startTime: Date?,
        @Param("endTime") endTime: Date?
    ): HomeData?

    fun selectUndoData(
        @Param("topId") topId: Long?
    ): UndoData?

}
