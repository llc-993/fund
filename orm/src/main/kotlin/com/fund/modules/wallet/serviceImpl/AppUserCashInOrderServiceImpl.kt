package com.fund.modules.wallet.serviceImpl;

import cn.hutool.core.date.DateUtil
import com.fund.modules.wallet.model.AppUserCashInOrder;
import com.fund.modules.wallet.mapper.AppUserCashInOrderMapper;
import com.fund.modules.wallet.service.AppUserCashInOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.common.entity.R
import com.fund.modules.agent.service.AppAgentRelationService
import com.fund.modules.cash.CashInReq
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.GeneratorIdUtil.generateId
import com.fund.utils.IpUtils
import com.fund.utils.RedisLockService
import org.springframework.stereotype.Service;
import java.time.LocalDateTime

/**
 * <p>
 * 用户充值订单表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-18
 */
@Service
open class AppUserCashInOrderServiceImpl(
    private val agentRelationService: AppAgentRelationService,
    private val appUserWalletV2Service: AppUserWalletV2Service,
    private val appUserService: AppUserService
) : ServiceImpl<AppUserCashInOrderMapper, AppUserCashInOrder>(), AppUserCashInOrderService {

    override fun request(userId: Long, req: CashInReq): R<Any> {
        val lockKey = RedisKeys.LOCK_CASH_IN_REQUEST + userId
        // 锁定同一个用户提交
        return RedisLockService.lockTransaction(lockKey) {
            val appUser: AppUser = appUserService.getById(userId)
            // 生成订单
            val order = AppUserCashInOrder()
            order.userId = userId
            order.userGroup = appUser.userGroup
            order.userAccount = appUser.userAccount ?: ""
            order.mobilePhone = appUser.mobilePhone ?: ""
            order.topUserId = agentRelationService.getTopIdByUserIdFromCache(userId)
            order.ip = IpUtils.getIpAddr()
            order.orderNo = "ci" + generateId()
            order.applyTime = LocalDateTime.now()
            order.applyAmount = req.amount
            // 订单类型   1待处理 2已锁定 3  已取消 4 已拒绝 5 已成功
            order.cashStatus = 1
            save(order)
            R.success()
        }
    }

}
