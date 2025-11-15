package com.fund.modules.wallet.serviceImpl;

import com.fund.modules.wallet.model.AppUserCashOutOrder;
import com.fund.modules.wallet.mapper.AppUserCashOutOrderMapper;
import com.fund.modules.wallet.service.AppUserCashOutOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.cash.CashOutReq
import com.fund.modules.conf.enum.AppConfigCode
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import com.fund.modules.wallet.enum.GoldChangeEnum
import com.fund.modules.wallet.model.AppWalletOperationLog
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.modules.wallet.service.AppWalletOperationLogService
import com.fund.utils.GeneratorIdUtil
import com.fund.utils.I18nUtil
import com.fund.utils.RedisLockService
import org.springframework.stereotype.Service;
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

/**
 * <p>
 * 用户提现订单表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-18
 */
@Service
open class AppUserCashOutOrderServiceImpl(
    private val appUserService: AppUserService,
    private val appUserWalletV2Service: AppUserWalletV2Service,
    private val appConfigService: AppConfigService,
    private val i18nUtil: I18nUtil,
    private val appWalletOperationLogService: AppWalletOperationLogService
) : ServiceImpl<AppUserCashOutOrderMapper, AppUserCashOutOrder>(), AppUserCashOutOrderService {


    override fun request(userId: Long, req: CashOutReq): R<Any> {
        val lockKey = RedisKeys.LOCK_CASH_OUT_REQUEST + userId
        return RedisLockService.lockTransaction(lockKey) {

            // 检查资金密码
            val appUser: AppUser = appUserService.getById(userId)
                ?: throw BusinessException("login_information_lost")

            if (req.moneyPassword != appUser.showMoneyPassword) {
                throw BusinessException("incorrect_password")
            }
            if (!appUser.cashable!!) {
                // 禁止提现。
                throw BusinessException("operation_is_restricted")
            }

            val userWallet = appUserWalletV2Service
                .findWalletByUserAndType(userId, 0,
                    req.coinType!!)?: throw BusinessException("member_wallet_data_loss")

            // 获取提现金额配置
            val minAmount = appConfigService.getValueOrDefault(AppConfigCode.CASH_OUT_MIN_AMOUNT)?.toBigDecimalOrNull() ?: BigDecimal("10")
            val maxAmount = appConfigService.getValueOrDefault(AppConfigCode.CASH_OUT_MAX_AMOUNT)?.toBigDecimalOrNull() ?: BigDecimal("1000000")

            // 验证提现金额范围
            val amount = req.amount ?: throw BusinessException("cash_out_not_null")
            
            if (amount < minAmount) {
                throw BusinessException(i18nUtil.getMessage("cash_out_amount_too_low", minAmount.toPlainString()))
            }
            
            if (amount > maxAmount) {
                throw BusinessException(i18nUtil.getMessage("cash_out_amount_too_high", maxAmount.toPlainString()))
            }

            // 获取提现手续费率
            val feeRate = appConfigService.getValueOrDefault(AppConfigCode.CASH_OUT_FEE_RATE)?.toBigDecimalOrNull() ?: BigDecimal("0.01")
            
            // 计算手续费
            val fee = amount.multiply(feeRate).setScale(8, RoundingMode.DOWN)
            
            // 计算实际到账金额
            val actualAmount = amount.subtract(fee)
            
            // 验证余额是否足够（需要扣除申请金额）
            val availableBalance = userWallet.availableBalance ?: BigDecimal.ZERO
            if (amount > availableBalance) {
                throw BusinessException(i18nUtil.getMessage("insufficient_balance", availableBalance.toPlainString()))
            }

            // 创建提现订单
            val cashOutOrder = AppUserCashOutOrder().apply {
                this.userId = userId
                userGroup = 0
                userAccount = appUser.userAccount
                topUserId = appUser.topUserId
                orderNo = GeneratorIdUtil.generateId()
                applyTime = LocalDateTime.now()
                applyAmount = amount
                this.actualAmount = actualAmount
                this.fee = fee
                fullName = req.platformName
                netWork = req.coinType
                address = req.address
                mobilePhone = appUser.mobilePhone
                cashStatus = 1  // 1待处理
            }

            // 保存提现订单
            this.save(cashOutOrder)

            // 从可用余额转移到冻结余额
            val beforeAvailableBalance = userWallet.availableBalance ?: BigDecimal.ZERO
            val beforeFrozenBalance = userWallet.frozenBalance ?: BigDecimal.ZERO
            
            userWallet.availableBalance = beforeAvailableBalance.subtract(amount)
            userWallet.frozenBalance = beforeFrozenBalance.add(amount)
            
            val updateSuccess = appUserWalletV2Service.updateById(userWallet)
            if (!updateSuccess) {
                throw BusinessException("deduct_balance_failed")
            }

            // 记录钱包操作日志
            val operationLog = AppWalletOperationLog().apply {
                serialNo = GeneratorIdUtil.generateId()
                this.userId = userId
                walletType = 0
                operationType = GoldChangeEnum.CASH_OUT_REQUEST.code
                this.amount = amount.negate()  // 负数表示扣减
                beforeBalance = beforeAvailableBalance
                afterBalance = userWallet.availableBalance
                relatedId = cashOutOrder.id
                relatedType = GoldChangeEnum.CASH_OUT_REQUEST.name
                createTime = LocalDateTime.now()
                status = 1  // 成功
                remark = "提现申请: ${cashOutOrder.orderNo}, 申请金额: ${amount.toPlainString()}, 手续费: ${fee.toPlainString()}, 实际到账: ${actualAmount.toPlainString()}, 冻结余额变化: ${beforeFrozenBalance.toPlainString()} -> ${userWallet.frozenBalance?.toPlainString()}"
            }
            appWalletOperationLogService.save(operationLog)

            R.success(mapOf(
                "orderNo" to cashOutOrder.orderNo,
                "applyAmount" to amount.toPlainString(),
                "fee" to fee.toPlainString(),
                "actualAmount" to actualAmount.toPlainString()
            ))
        }
    }

}
