package com.fund.modules.wallet.serviceImpl;

import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.fund.modules.wallet.model.AppUserCashInOrder;
import com.fund.modules.wallet.mapper.AppUserCashInOrderMapper;
import com.fund.modules.wallet.service.AppUserCashInOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.agent.service.AppAgentRelationService
import com.fund.modules.cash.CashInReq
import com.fund.modules.cash.CashInReviewReq
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import com.fund.modules.wallet.enum.GoldChangeEnum
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
            order.imgUrl = req.imgUrl
            // 订单类型   1待处理 2已锁定 3  已取消 4 已拒绝 5 已成功
            order.cashStatus = 1
            order.depositCode = req.depositCode
            save(order)
            R.success()
        }
    }

    override fun review(req: CashInReviewReq) {
        RedisLockService.transaction block@{
            val order = this.getById(req.id) ?: throw BusinessException("订单不存在")
            if (order.cashStatus != 1) {
                throw BusinessException("订单状态已变更")
            }
            if (!req.pass!!) {
                // 拒绝
                update(
                    KtUpdateWrapper(AppUserCashInOrder())
                        .eq(AppUserCashInOrder::id, req.id) // 订单类型   1待处理 2已锁定 3  已取消 4 已拒绝 5 已成功
                        .set(AppUserCashInOrder::cashStatus, 4)
                        .set(StrUtil.isNotBlank(req.reason), AppUserCashInOrder::reason, req.reason)
                        .set(AppUserCashInOrder::remitTime, DateUtil.date())
                )
                return@block
            }

            val userId = order.userId ?: throw BusinessException("用户ID不存在")
            val amount = order.applyAmount ?: throw BusinessException("充值金额不存在")
            val currencyCode = req.depositCode?.takeIf { it.isNotBlank() } 
                ?: order.depositCode?.takeIf { it.isNotBlank() } 
                ?: "HKD"
            
            val success = appUserWalletV2Service.addAvailableBalance(
                userId = userId,
                walletType = 0,
                currencyCode = currencyCode,
                amount = amount,
                operationType = GoldChangeEnum.CASH_IN,
                remark = """
                    用户id:${userId},
                    操作：后台审核,
                    金额:${amount}
                """.trimIndent()
            )
            
            if (!success) {
                throw BusinessException("充值到账失败")
            }
            update(
                KtUpdateWrapper(AppUserCashInOrder())
                    .eq(AppUserCashInOrder::id, req.id) // 订单类型   1待处理 2已锁定 3  已取消 4 已拒绝 5 已成功
                    .set(AppUserCashInOrder::cashStatus, 5)
                    .set(AppUserCashInOrder::remitTime, DateUtil.date())
            )
        }
    }

}
