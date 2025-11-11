package com.fund.modules.financial.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fund.modules.financial.model.FinancialProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 理财产品信息表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
@Mapper
interface FinancialProductMapper : BaseMapper<FinancialProduct> {

}
