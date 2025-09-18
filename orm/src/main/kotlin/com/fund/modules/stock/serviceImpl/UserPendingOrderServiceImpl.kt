package com.fund.modules.stock.serviceImpl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.modules.stock.mapper.UserPendingOrderMapper
import com.fund.modules.stock.model.UserPendingOrder
import com.fund.modules.stock.service.UserPendingOrderService
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户挂单表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
@Service
open class UserPendingOrderServiceImpl : ServiceImpl<UserPendingOrderMapper, UserPendingOrder>(),UserPendingOrderService {

}
