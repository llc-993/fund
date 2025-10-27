package com.fund.modules.financial.serviceImpl;

import com.fund.modules.financial.model.FinancialOrder;
import com.fund.modules.financial.mapper.FinancialOrderMapper;
import com.fund.modules.financial.service.FinancialOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 理财订单 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-27
 */
@Service
open class FinancialOrderServiceImpl : ServiceImpl<FinancialOrderMapper, FinancialOrder>(), FinancialOrderService {

}
