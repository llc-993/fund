package com.fund.modules.quotation.mapper

import com.fund.modules.quotation.model.UserQuotationControl
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper

/**
 * 用户行情调控表 Mapper 接口
 */
@Mapper
interface UserQuotationControlMapper : BaseMapper<UserQuotationControl>
