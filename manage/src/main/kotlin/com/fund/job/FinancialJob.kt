package com.fund.job

import com.fund.modules.financial.service.FinancialOrderService
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * 理财产品定时任务
 * - 每日凌晨0:15结算昨日理财收益并处理到期订单
 */
@Component
class FinancialJob(
    private val financialOrderService: FinancialOrderService
) {
    private val logger = KotlinLogging.logger {}
    
    /**
     * 每天凌晨0:15执行，结算昨日理财收益
     * - 将收益计入用户冻结余额，更新订单累计收益、最后收益和结算次数
     * - 同时处理到期订单，将累计收益转入可用余额，更新订单状态为已过期
     */
    @Scheduled(cron = "0 15 0 * * ?")  // 每天0:15执行
    fun settleInterest() {
        logger.info { "开始执行理财收益结算定时任务" }
        
        try {
            val settlementDate = LocalDate.now().minusDays(1)  // 结算昨天的收益
            val processedCount = financialOrderService.settleInterest(settlementDate)
            
            logger.info { "理财收益结算完成，处理订单数: $processedCount, 结算日期: $settlementDate" }
        } catch (e: Exception) {
            logger.error(e) { "理财收益结算任务异常" }
        }
    }
}