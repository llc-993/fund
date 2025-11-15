package com.fund.modules.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.notice.model.AppNotice;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统公告 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-11-14
 */
@Mapper
interface AppNoticeMapper : BaseMapper<AppNotice> {

}
