package com.fund.modules.aiquant.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.common.RedisKeys
import com.fund.exception.BusinessException
import com.fund.modules.aiquant.AiQuantCyclePhase
import com.fund.modules.aiquant.AiQuantUserSummaryVo
import com.fund.modules.aiquant.mapper.AppAiQuantCycleMapper
import com.fund.modules.aiquant.model.AppAiQuantCycle
import com.fund.modules.aiquant.model.AppAiQuantOrder
import com.fund.modules.aiquant.request.AiQuantAuditManageReq
import com.fund.modules.aiquant.request.AiQuantCyclePageManageReq
import com.fund.modules.aiquant.request.AiQuantFinishManageReq
import com.fund.modules.aiquant.request.AiQuantReserveReq
import com.fund.modules.aiquant.service.AppAiQuantCycleService
import com.fund.modules.aiquant.service.AppAiQuantGlobalConfigService
import com.fund.modules.aiquant.service.AppAiQuantOrderService
import com.fund.modules.wallet.enum.GoldChangeEnum
import com.fund.modules.wallet.model.AppUserWalletV2
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.GeneratorIdUtil
import com.fund.utils.RedisLockService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
open class AppAiQuantCycleServiceImpl(
    private val walletService: AppUserWalletV2Service,
    private val globalConfigService: AppAiQuantGlobalConfigService,
    @Lazy private val orderService: AppAiQuantOrderService,
) : ServiceImpl<AppAiQuantCycleMapper, AppAiQuantCycle>(),
    AppAiQuantCycleService {

    override fun submitReserve(userId: Long, req: AiQuantReserveReq): AppAiQuantCycle {
        val lockKey = RedisKeys.LOCK_AI_QUANT_CYCLE + userId
        return RedisLockService.lockTransaction(lockKey) {
            if (hasActiveReserve(userId)) {
                throw BusinessException("已在预约或处理中周期，请先完成后再申请")
            }
            val amount = req.amount.setScale(16, RoundingMode.HALF_UP)
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw BusinessException("预约金额必须大于0")
            }
            val cfg = globalConfigService.loadOrCreate()
            val minReserve = cfg.minReserveAmount ?: BigDecimal.ZERO
            if (amount.compareTo(minReserve) < 0) {
                throw BusinessException("预约金额低于最低限额")
            }
            val currency = req.currencyCode?.takeIf { it.isNotBlank() } ?: "HKD"
            val wallet = walletService.findWalletByUserAndType(userId, 0, currency)
                ?: throw BusinessException("钱包不存在")
            val walletType = wallet.walletType ?: 0
            val currencyCode = wallet.currencyCode ?: throw BusinessException("钱包币种异常")

            walletService.freezeAiQuantPrincipal(
                userId = userId,
                walletType = walletType,
                currencyCode = currencyCode,
                amount = amount,
                operationType = GoldChangeEnum.AI_QUANT_RESERVE_FREEZE,
                remark = "AI量化预约冻结本金",
                relatedCycleId = null,
            )

            val cycle = AppAiQuantCycle().apply {
                this.userId = userId
                walletId = wallet.id
                cycleNo = "AQC${GeneratorIdUtil.generateId()}"
                requestAmount = amount
                phase = AiQuantCyclePhase.PENDING_AUDIT
            }
            if (!save(cycle)) {
                throw BusinessException("创建周期失败")
            }

            cycle
        }
    }

    override fun audit(adminId: Long, req: AiQuantAuditManageReq): AppAiQuantCycle {
        val lockKey = RedisKeys.LOCK_AI_QUANT_ORDER + req.cycleId
        return RedisLockService.lockTransaction(lockKey) {
            val cycle =
                getById(req.cycleId) ?: throw BusinessException("周期不存在")
            if (cycle.phase != AiQuantCyclePhase.PENDING_AUDIT) {
                throw BusinessException("仅待审周期可操作审核")
            }
            val wallet = walletService.getById(cycle.walletId!!)
                ?: throw BusinessException("钱包不存在")
            val wt = wallet.walletType ?: 0
            val currencyCode = wallet.currencyCode ?: throw BusinessException("钱包币种异常")
            val uid = cycle.userId!!

            if (req.passed) {
                val approvedRaw = req.approvedAmount ?: throw BusinessException("通过审核时必须填写核定本金")
                val approved = approvedRaw.setScale(16, RoundingMode.HALF_UP)
                val requestAmt = cycle.requestAmount!!
                if (approved.compareTo(BigDecimal.ZERO) <= 0 || approved.compareTo(requestAmt) > 0) {
                    throw BusinessException("核定本金必须在 (0, 预约金额] 范围内")
                }
                val excess = requestAmt.subtract(approved)
                if (excess.compareTo(BigDecimal.ZERO) > 0) {
                    walletService.releaseAiQuantPrincipal(
                        userId = uid,
                        walletType = wt,
                        currencyCode = currencyCode,
                        amount = excess,
                        operationType = GoldChangeEnum.AI_QUANT_RESERVE_REJECT,
                        remark = "审核通过退回预约超额冻结",
                        relatedCycleId = cycle.id,
                    )
                }
                cycle.approvedAmount = approved
                cycle.phase = AiQuantCyclePhase.PROCESSING
                cycle.auditAdminId = adminId
                cycle.auditTime = LocalDateTime.now()
                cycle.rejectReason = null
                walletService.accumulateAiQuantStats(
                    userId = uid,
                    walletType = wt,
                    currencyCode = currencyCode,
                    netProfitDelta = BigDecimal.ZERO,
                    feeDelta = BigDecimal.ZERO,
                    investDelta = approved,
                )
            } else {
                walletService.releaseAiQuantPrincipal(
                    userId = uid,
                    walletType = wt,
                    currencyCode = currencyCode,
                    amount = cycle.requestAmount!!,
                    operationType = GoldChangeEnum.AI_QUANT_RESERVE_REJECT,
                    remark = req.rejectReason ?: "审核驳回解冻",
                    relatedCycleId = cycle.id,
                )
                cycle.phase = AiQuantCyclePhase.REJECTED
                cycle.rejectReason = req.rejectReason ?: "驳回"
                cycle.auditAdminId = adminId
                cycle.auditTime = LocalDateTime.now()
            }
            if (!updateById(cycle)) {
                throw BusinessException("更新审核结果失败")
            }
            cycle
        }
    }

    override fun finish(adminId: Long, req: AiQuantFinishManageReq): AppAiQuantCycle {
        val lockKey = RedisKeys.LOCK_AI_QUANT_ORDER + req.cycleId
        return RedisLockService.lockTransaction(lockKey) {
            val cycle =
                getById(req.cycleId) ?: throw BusinessException("周期不存在")
            if (cycle.phase != AiQuantCyclePhase.PROCESSING) {
                throw BusinessException("仅处理中周期可完结")
            }
            val oid = cycle.linkedOrderId ?: throw BusinessException("周期未绑定展示订单")
            val orderEntity = orderService.getById(oid) ?: throw BusinessException("订单不存在")
            if (orderEntity.sellTime == null || orderEntity.sellPrice == null) {
                throw BusinessException("请先补齐卖出时间与卖出价")
            }
            orderService.calcAndApplyProfit(orderEntity)
            val gross = orderEntity.profitAmount ?: BigDecimal.ZERO
            val fee = orderEntity.feeAmount ?: BigDecimal.ZERO
            val net = gross.subtract(fee).setScale(16, RoundingMode.HALF_UP)

            val wallet = walletService.getById(cycle.walletId!!)
                ?: throw BusinessException("钱包不存在")
            val wt = wallet.walletType ?: 0
            val currencyCode = wallet.currencyCode ?: throw BusinessException("钱包币种异常")
            val uid = cycle.userId!!
            val principal = cycle.approvedAmount ?: throw BusinessException("核定本金缺失")

            walletService.releaseAiQuantPrincipal(
                userId = uid,
                walletType = wt,
                currencyCode = currencyCode,
                amount = principal,
                operationType = GoldChangeEnum.AI_QUANT_PRINCIPAL_RELEASE,
                remark = "AI量化周期完成释放本金",
                relatedCycleId = cycle.id,
            )
            walletService.settleAiQuantProfit(
                userId = uid,
                walletType = wt,
                currencyCode = currencyCode,
                amount = gross,
                operationType = GoldChangeEnum.AI_QUANT_PROFIT_SETTLE,
                remark = "AI量化毛利结算",
                relatedCycleId = cycle.id,
            )
            if (fee.compareTo(BigDecimal.ZERO) > 0) {
                walletService.settleAiQuantProfit(
                    userId = uid,
                    walletType = wt,
                    currencyCode = currencyCode,
                    amount = fee.negate(),
                    operationType = GoldChangeEnum.AI_QUANT_FEE_DEDUCT,
                    remark = "AI量化盈利手续费扣减",
                    relatedCycleId = cycle.id,
                )
            }
            walletService.accumulateAiQuantStats(
                userId = uid,
                walletType = wt,
                currencyCode = currencyCode,
                netProfitDelta = net,
                feeDelta = fee,
                investDelta = BigDecimal.ZERO,
            )

            orderEntity.userVisible = 1
            if (!orderService.updateById(orderEntity)) {
                throw BusinessException("更新订单可见性失败")
            }

            cycle.phase = AiQuantCyclePhase.FINISHED
            cycle.finishTime = LocalDateTime.now()
            cycle.profitAmount = net
            cycle.feeAmount = fee

            if (!updateById(cycle)) {
                throw BusinessException("更新周期完结状态失败")
            }
            cycle
        }
    }

    override fun listUserCurrent(userId: Long): List<AppAiQuantCycle> {
        return list(
            KtQueryWrapper(AppAiQuantCycle())
                .eq(AppAiQuantCycle::userId, userId)
                .`in`(AppAiQuantCycle::phase, listOf(AiQuantCyclePhase.PENDING_AUDIT, AiQuantCyclePhase.PROCESSING))
                .orderByDesc(AppAiQuantCycle::createTime),
        )
    }

    override fun listUserHistory(userId: Long): List<Pair<AppAiQuantCycle, AppAiQuantOrder?>> {
        val cycles = list(
            KtQueryWrapper(AppAiQuantCycle())
                .eq(AppAiQuantCycle::userId, userId)
                .eq(AppAiQuantCycle::phase, AiQuantCyclePhase.FINISHED)
                .orderByDesc(AppAiQuantCycle::finishTime),
        )
        return cycles.map { c ->
            val oid = c.linkedOrderId ?: return@map c to null
            val ord = orderService.getById(oid)
            c to ord?.takeIf { (it.userVisible ?: 0) == 1 }
        }
    }

    override fun summaryForUser(userId: Long): AiQuantUserSummaryVo {
        val wallets = walletService.list(
            KtQueryWrapper(AppUserWalletV2()).eq(AppUserWalletV2::userId, userId),
        )
        val z = BigDecimal.ZERO.setScale(16, RoundingMode.HALF_UP)
        fun sum(sel: (AppUserWalletV2) -> BigDecimal?): BigDecimal =
            wallets.fold(z) { acc, w -> acc.add((sel(w) ?: BigDecimal.ZERO).setScale(16, RoundingMode.HALF_UP)) }
        val active = count(
            KtQueryWrapper(AppAiQuantCycle())
                .eq(AppAiQuantCycle::userId, userId)
                .`in`(AppAiQuantCycle::phase, listOf(AiQuantCyclePhase.PENDING_AUDIT, AiQuantCyclePhase.PROCESSING)),
        ).toInt()
        return AiQuantUserSummaryVo(
            aiQuantFreeze = sum { it.aiQuantFreeze },
            aiQuantTotalInvest = sum { it.aiQuantTotalInvest },
            aiQuantTotalProfit = sum { it.aiQuantTotalProfit },
            aiQuantTotalFee = sum { it.aiQuantTotalFee },
            activeReserveCount = active,
        )
    }

    override fun managePage(query: AiQuantCyclePageManageReq): Page<AppAiQuantCycle> {
        val page = Page<AppAiQuantCycle>(query.current, query.size)
        val w = KtQueryWrapper(AppAiQuantCycle()).orderByDesc(AppAiQuantCycle::createTime)
        query.userId?.let { w.eq(AppAiQuantCycle::userId, it) }
        query.phase?.let { w.eq(AppAiQuantCycle::phase, it) }
        return page(page, w)
    }

    private fun hasActiveReserve(userId: Long): Boolean {
        return count(
            KtQueryWrapper(AppAiQuantCycle())
                .eq(AppAiQuantCycle::userId, userId)
                .`in`(AppAiQuantCycle::phase, listOf(AiQuantCyclePhase.PENDING_AUDIT, AiQuantCyclePhase.PROCESSING)),
        ) > 0
    }
}
