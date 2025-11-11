package com.fund.modules.financial.serviceImpl;

import com.fund.modules.financial.model.FinancialProduct;
import com.fund.modules.financial.mapper.FinancialProductMapper;
import com.fund.modules.financial.service.FinancialProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 理财产品信息表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
@Service
open class FinancialProductServiceImpl : ServiceImpl<FinancialProductMapper, FinancialProduct>(), FinancialProductService {

}
