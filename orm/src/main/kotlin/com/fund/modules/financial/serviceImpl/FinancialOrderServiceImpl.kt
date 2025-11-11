package com.fund.modules.financial.serviceImpl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.common.RedisKeys
import com.fund.exception.BusinessException
import com.fund.modules.financial.FinancialOrderPurchaseRequest
import com.fund.modules.financial.mapper.FinancialOrderMapper
import com.fund.modules.financial.model.FinancialOrder
import com.fund.modules.financial.model.FinancialProduct
import com.fund.modules.financial.service.FinancialOrderService
import com.fund.modules.financial.service.FinancialProductService
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.GeneratorIdUtil
import com.fund.utils.I18nUtil
import com.fund.utils.RedisLockService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * <p>
 * 理财订单表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-11-10
 */
@Service
open class FinancialOrderServiceImpl(
    private val financialProductService: FinancialProductService,
    private val appUserWalletV2Service: AppUserWalletV2Service,
    private val i18nUtil: I18nUtil
) : ServiceImpl<FinancialOrderMapper, FinancialOrder>(), FinancialOrderService {

    private val logger = KotlinLogging.logger {}
    
    companion object {
        private const val PRODUCT_STATUS_AVAILABLE: Byte = 1
        private const val ORDER_STATUS_ACTIVE: Byte = 1
        private const val ORDER_STATUS_CLOSED: Byte = 2
        private const val ORDER_STATUS_EXPIRED: Byte = 3
        private const val WALLET_OPERATION_TYPE = "FINANCIAL_PURCHASE"
        private const val WALLET_INTEREST_TYPE = "FINANCIAL_INTEREST"
        private const val WALLET_EXPIRE_TYPE = "FINANCIAL_EXPIRE"
        
        // 一年按365天计算
        private const val DAYS_PER_YEAR = 365
        
        // 计算精度
        private const val CALCULATION_SCALE = 8
    }

    override fun purchase(userId: Long, request: FinancialOrderPurchaseRequest): FinancialOrder {
        // 用户级锁，防止并发购买
        val lockKey = RedisKeys.LOCK_FINANCIAL_PURCHASE + userId
        
        return RedisLockService.lockTransaction(lockKey) block@ {
            // 1. 校验产品是否存在且可购买
            val productId = request.productId
            val product = financialProductService.getById(productId)
                ?: throw BusinessException("financial_product_not_exists")
                
            if (product.status != PRODUCT_STATUS_AVAILABLE) {
                throw BusinessException("financial_purchase_product_status_invalid")
            }
            
            // 2. 校验金额
            val amount = request.amount
            if (amount <= BigDecimal.ZERO) {
                throw BusinessException("financial_purchase_amount_invalid")
            }
            
            // 3. 校验最小/最大金额限制
            product.limitMinAmount?.let {
                if (amount < it) {
                    throw BusinessException(i18nUtil.getMessage(
                        "financial_purchase_amount_less_than_min", 
                        null, 
                        it.stripTrailingZeros().toPlainString()
                    ))
                }
            }
            
            product.limitMaxAmount?.let {
                if (amount > it) {
                    throw BusinessException(i18nUtil.getMessage(
                        "financial_purchase_amount_greater_than_max", 
                        null, 
                        it.stripTrailingZeros().toPlainString()
                    ))
                }
            }
            
            // 4. 获取钱包并校验余额
            val walletType = request.walletType ?: 0
            val coin = product.coin ?: "USD"
            
            val wallet = appUserWalletV2Service.findWalletByUserAndType(userId, walletType.toInt(), coin)
                ?: throw BusinessException("financial_purchase_wallet_not_found")
                
            val availableBalance = wallet.availableBalance ?: BigDecimal.ZERO
            if (availableBalance < amount) {
                throw BusinessException(i18nUtil.getMessage(
                    "insufficient_balance", 
                    null, 
                    availableBalance.stripTrailingZeros().toPlainString()
                ))
            }
            
            // 5. 扣减钱包余额
            val remark = i18nUtil.getMessage(
                "financial_purchase_wallet_remark", 
                null, 
                product.productCode ?: product.id.toString(), 
                amount.stripTrailingZeros().toPlainString()
            )
            
            val deductSuccess = appUserWalletV2Service.subtractAvailableBalance(
                userId = userId,
                walletType = walletType.toInt(),
                currencyCode = coin,
                amount = amount,
                operationType = WALLET_OPERATION_TYPE,
                remark = remark
            )
            
            if (!deductSuccess) {
                throw BusinessException("financial_purchase_balance_deduct_failed")
            }
            
            // 6. 创建订单
            val now = LocalDateTime.now()
            val order = FinancialOrder().apply {
                this.userId = userId
                this.orderNo = GeneratorIdUtil.generateId()
                this.productId = product.id
                this.productCode = product.productCode
                this.productName = product.title
                this.investAmount = amount
                this.investPeriod = product.days
                this.coin = coin
                this.rateType = product.rateType
                this.minRate = product.minRate
                this.maxRate = product.maxRate
                this.defaultRate = product.defaultRate
                this.actualRate = product.avgRate ?: product.defaultRate
                this.orderStatus = ORDER_STATUS_ACTIVE
                this.startTime = now
                
                // 设置下次结算时间（次日）
                this.nextSettleTime = now.plusDays(1)
                
                // 设置到期时间（固定期限产品）
                if (product.days != null && product.days!! > 0) {
                    this.expireTime = now.plusDays(product.days!!.toLong())
                }
                
                this.accumulatedProfit = BigDecimal.ZERO
                this.lastProfit = BigDecimal.ZERO
                this.settledCount = 0
                this.settleCycle = "daily"  // 默认每日结算
                this.walletType = request.walletType
                this.remark = request.remark
                this.applyTime = now
            }
            
            // 7. 保存订单
            if (!this.save(order)) {
                throw BusinessException("financial_purchase_create_failed")
            }
            
            // 8. 更新产品已购金额
            try {
                val currentPurchasedAmount = product.purchasedAmount ?: BigDecimal.ZERO
                val newPurchasedAmount = currentPurchasedAmount.add(amount)
                val remainAmount = product.totalInvestAmount?.subtract(newPurchasedAmount)
                
                product.purchasedAmount = newPurchasedAmount
                product.remainAmount = remainAmount
                product.buyPurchase = (product.buyPurchase ?: 0) + 1
                
                financialProductService.updateById(product)
            } catch (e: Exception) {
                // 更新产品统计数据失败不影响订单创建
                logger.error(e) { "更新产品统计数据失败: productId=${product.id}" }
            }
            
            return@block order
        }
    }
    
    override fun settleInterest(settlementDate: LocalDate): Int {
        logger.info { "开始结算理财收益，结算日期: $settlementDate" }
        var successCount = 0
        var expiredCount = 0
        val today = LocalDate.now()
        val todayStart = today.atStartOfDay()
        
        try {
            // 1. 查询所有生效中的订单
            val orders = this.list(
                KtQueryWrapper(FinancialOrder())
                    .eq(FinancialOrder::orderStatus, ORDER_STATUS_ACTIVE)
            )
            
            if (orders.isEmpty()) {
                logger.info { "没有需要结算的订单" }
                return 0
            }
            
            // 2. 按用户ID分组处理
            val ordersByUser = orders.groupBy { it.userId }
            
            // 3. 逐个用户处理
            for ((userId, userOrders) in ordersByUser) {
                if (userId == null) continue
                
                // 使用用户级锁
                val lockKey = RedisKeys.LOCK_FINANCIAL_PURCHASE + userId
                RedisLockService.lockTransaction(lockKey) {
                    for (order in userOrders) {
                        try {
                            // 计算当日收益
                            val dailyProfit = calculateDailyProfit(order)
                            
                            // 获取钱包
                            val coin = order.coin ?: "USD"
                            val walletType = order.walletType?.toInt() ?: 0
                            
                            val wallet = appUserWalletV2Service.findWalletByUserAndType(userId, walletType, coin)
                                ?: continue
                            
                            // 检查订单是否到期
                            val isExpired = order.expireTime != null && order.expireTime!!.isBefore(todayStart)
                            
                            if (isExpired) {
                                // 处理到期订单
                                
                                // 1. 更新订单最后收益和累计收益
                                order.lastProfit = dailyProfit
                                order.accumulatedProfit = (order.accumulatedProfit ?: BigDecimal.ZERO).add(dailyProfit)
                                order.settledCount = (order.settledCount ?: 0) + 1
                                
                                // 2. 计算总收益
                                val totalProfit = order.accumulatedProfit ?: BigDecimal.ZERO
                                
                                // 3. 将收益解冻到可用余额
                                if (totalProfit > BigDecimal.ZERO) {
                                    val remark = "理财产品到期收益: ${order.orderNo}, 总收益: ${totalProfit.stripTrailingZeros().toPlainString()}"
                                    
                                    val addSuccess = appUserWalletV2Service.addAvailableBalance(
                                        userId = userId,
                                        walletType = walletType,
                                        currencyCode = coin,
                                        amount = totalProfit,
                                        operationType = WALLET_EXPIRE_TYPE,
                                        remark = remark
                                    )
                                    
                                    if (!addSuccess) {
                                        logger.error { "添加到期收益失败: orderId=${order.id}, userId=$userId, amount=$totalProfit" }
                                        continue
                                    }
                                }
                                
                                // 4. 更新订单状态为已过期
                                order.orderStatus = ORDER_STATUS_EXPIRED
                                order.closeTime = LocalDateTime.now()
                                
                                // 5. 更新订单
                                this.updateById(order)
                                expiredCount++
                                
                            } else {
                                // 处理正常结算
                                
                                // 1. 更新订单信息
                                order.lastProfit = dailyProfit
                                order.accumulatedProfit = (order.accumulatedProfit ?: BigDecimal.ZERO).add(dailyProfit)
                                order.settledCount = (order.settledCount ?: 0) + 1
                                order.nextSettleTime = LocalDateTime.now().plusDays(1)
                                
                                // 2. 将收益添加到冻结余额
                                val remark = "理财收益: ${order.orderNo}, 金额: ${dailyProfit.stripTrailingZeros().toPlainString()}"
                                
                                val freezeSuccess = appUserWalletV2Service.freezeBalance(
                                    userId = userId,
                                    walletType = walletType,
                                    currencyCode = coin,
                                    amount = dailyProfit,
                                    operationType = WALLET_INTEREST_TYPE,
                                    remark = remark
                                )
                                
                                if (!freezeSuccess) {
                                    logger.error { "冻结收益失败: orderId=${order.id}, userId=$userId, amount=$dailyProfit" }
                                    continue
                                }
                                
                                // 3. 更新订单
                                this.updateById(order)
                                successCount++
                            }
                            
                        } catch (e: Exception) {
                            logger.error(e) { "结算订单收益失败: orderId=${order.id}, userId=$userId" }
                        }
                    }
                }
            }
            
        } catch (e: Exception) {
            logger.error(e) { "结算理财收益异常" }
        }
        
        logger.info { "理财收益结算完成，成功处理订单数: $successCount, 到期处理订单数: $expiredCount" }
        return successCount + expiredCount
    }

    
    /**
     * 计算订单日收益
     * 收益 = 投资金额 * 日利率
     * 日利率 = 年利率 / 365
     */
    private fun calculateDailyProfit(order: FinancialOrder): BigDecimal {
        val investAmount = order.investAmount ?: return BigDecimal.ZERO
        val actualRate = order.actualRate ?: order.defaultRate ?: return BigDecimal.ZERO
        
        // 计算日利率 = 年利率 / 365
        val dailyRate = actualRate.divide(BigDecimal(DAYS_PER_YEAR), CALCULATION_SCALE, RoundingMode.DOWN)
        
        // 计算日收益 = 投资金额 * 日利率
        return investAmount.multiply(dailyRate).setScale(6, RoundingMode.DOWN)
    }
}