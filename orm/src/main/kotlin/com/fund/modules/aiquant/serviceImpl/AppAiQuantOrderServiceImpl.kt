package com.fund.modules.aiquant.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.common.RedisKeys
import com.fund.exception.BusinessException
import com.fund.modules.aiquant.AiQuantCyclePhase
import com.fund.modules.aiquant.mapper.AppAiQuantOrderMapper
import com.fund.modules.aiquant.model.AppAiQuantOrder
import com.fund.modules.aiquant.request.AiQuantOrderCreateManageReq
import com.fund.modules.aiquant.request.AiQuantOrderPageManageReq
import com.fund.modules.aiquant.request.AiQuantOrderUpdateManageReq
import com.fund.modules.aiquant.service.AppAiQuantCycleService
import com.fund.modules.aiquant.service.AppAiQuantGlobalConfigService
import com.fund.modules.aiquant.service.AppAiQuantOrderService
import com.fund.modules.stock.service.StockService
import com.fund.utils.GeneratorIdUtil
import com.fund.utils.RedisLockService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
open class AppAiQuantOrderServiceImpl(
    @Lazy private val cycleService: AppAiQuantCycleService,
    private val globalConfigService: AppAiQuantGlobalConfigService,
    private val stockService: StockService,
) : ServiceImpl<AppAiQuantOrderMapper, AppAiQuantOrder>(), AppAiQuantOrderService {

    override fun createByAdmin(adminId: Long, req: AiQuantOrderCreateManageReq): AppAiQuantOrder {
        val lockKey = RedisKeys.LOCK_AI_QUANT_ORDER + req.cycleId
        return RedisLockService.lockTransaction(lockKey) {
            val cycle =
                cycleService.getById(req.cycleId) ?: throw BusinessException("周期不存在")
            if (cycle.phase != AiQuantCyclePhase.PROCESSING) {
                throw BusinessException("仅处理中的周期可以建单")
            }
            if (cycle.linkedOrderId != null) {
                throw BusinessException("该周期已绑定订单")
            }
            val approved = cycle.approvedAmount ?: throw BusinessException("核定本金为空")
            if (req.buyPrice!!.compareTo(BigDecimal.ZERO) <= 0) {
                throw BusinessException("买入价必须大于0")
            }
            val stock = stockService.getStockById(req.stockId)
            val cfg = globalConfigService.loadOrCreate()
            val order = AppAiQuantOrder().apply {
                cycleId = cycle.id
                userId = cycle.userId
                orderNo = "AQO${GeneratorIdUtil.generateId()}"
                stockId = stock.id!!
                stockName = stock.name
                symbol = stock.symbol ?: throw BusinessException("股票代码缺失")
                market = stock.flag ?: "UNKNOWN"
                buyTime = req.buyTime
                buyPrice = req.buyPrice
                buyAmount = approved
                feeRate = cfg.feeRate ?: BigDecimal.ZERO
                feeAmount = BigDecimal.ZERO.setScale(16, RoundingMode.HALF_UP)
                userVisible = 0
                createAdminId = adminId
                remark = req.remark
            }
            if (!save(order)) {
                throw BusinessException("保存订单失败")
            }
            cycle.linkedOrderId = order.id
            if (!cycleService.updateById(cycle)) {
                throw BusinessException("绑定周期订单失败")
            }
            order
        }
    }

    override fun updateByAdmin(adminId: Long, req: AiQuantOrderUpdateManageReq): AppAiQuantOrder {
        val existing = getById(req.orderId) ?: throw BusinessException("订单不存在")
        val lockKey = RedisKeys.LOCK_AI_QUANT_ORDER + existing.cycleId
        return RedisLockService.lockTransaction(lockKey) {
            val loaded = getById(req.orderId) ?: throw BusinessException("订单不存在")
            loaded.sellTime = req.sellTime ?: loaded.sellTime
            loaded.sellPrice = req.sellPrice ?: loaded.sellPrice
            loaded.remark = req.remark ?: loaded.remark
            if (!updateById(loaded)) {
                throw BusinessException("更新订单失败")
            }
            loaded
        }
    }

    override fun managePage(query: AiQuantOrderPageManageReq): Page<AppAiQuantOrder> {
        val page = Page<AppAiQuantOrder>(query.current, query.size)
        val w = KtQueryWrapper(AppAiQuantOrder())
            .orderByDesc(AppAiQuantOrder::createTime)
        query.cycleId?.let { w.eq(AppAiQuantOrder::cycleId, it) }
        query.userId?.let { w.eq(AppAiQuantOrder::userId, it) }
        return page(page, w)
    }

    override fun calcAndApplyProfit(order: AppAiQuantOrder): AppAiQuantOrder {
        val bp = order.buyPrice ?: throw BusinessException("买入价不能为空")
        val sp = order.sellPrice ?: throw BusinessException("卖出价不能为空")
        val buyAmt = order.buyAmount ?: throw BusinessException("本金不能为空")
        if (buyAmt.compareTo(BigDecimal.ZERO) <= 0 || bp.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException("本金与买入价非法")
        }
        val qty = buyAmt.divide(bp, 16, RoundingMode.HALF_UP)
        val profit = sp.subtract(bp).multiply(qty).setScale(16, RoundingMode.HALF_UP)
        val feeRate = order.feeRate ?: BigDecimal.ZERO
        val fee = if (profit.compareTo(BigDecimal.ZERO) > 0) {
            profit.multiply(feeRate).setScale(16, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO.setScale(16, RoundingMode.HALF_UP)
        }
        order.positionQty = qty
        order.profitAmount = profit
        order.profitRate = buyAmt.takeIf { it.signum() != 0 }?.let { pr ->
            profit.divide(pr, 16, RoundingMode.HALF_UP)
        } ?: BigDecimal.ZERO.setScale(16, RoundingMode.HALF_UP)
        order.feeAmount = fee
        return order
    }
}
