package com.fund.modules.ipo.serviceImpl;

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.modules.ipo.model.StockSubscription;
import com.fund.modules.ipo.mapper.StockSubscriptionMapper;
import com.fund.modules.ipo.service.StockSubscriptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.conf.enum.AppConfigCode
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.ipo.IpoApplyRequest
import com.fund.modules.ipo.IpoUpdateRequest
import com.fund.modules.ipo.service.IpoService
import com.fund.modules.user.service.AppUserService
import com.fund.utils.GeneratorIdUtil
import com.fund.utils.I18nUtil
import com.fund.utils.RedisLockService
import mu.KotlinLogging
import org.springframework.stereotype.Service;
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 新股申购 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-07
 */
@Service
open class StockSubscriptionServiceImpl(
    private val ipoService: IpoService,
    private val appUserService: AppUserService,
    private val appConfigService: AppConfigService,
    private val i18nUtil: I18nUtil,
) : ServiceImpl<StockSubscriptionMapper, StockSubscription>(), StockSubscriptionService {

    private val logger = KotlinLogging.logger {}

    override fun apply(req: IpoApplyRequest, userId: Long): R<Any> {
        try {
            val key = RedisKeys.IPO_APPLY_LOCK_KEY + userId
            return RedisLockService.lockTransaction(key) block@{

                val appUser = appUserService.getById(userId) ?: throw BusinessException("user_null")

                val ipo = ipoService.getById(req.ipoId) ?: throw BusinessException("ipo_null")

                // 获取当前时间戳（毫秒）
                val currentTime = System.currentTimeMillis()

                // 如果设置了开放时间，检查 IPO 是否已经开始
                ipo.openDate?.let { openDate ->
                    if (currentTime < openDate) {
                        throw BusinessException("ipo_not_started")
                    }
                }

                // 如果设置了关闭时间，检查 IPO 是否已经结束
                ipo.closeDate?.let { closeDate ->
                    if (currentTime > closeDate) {
                        throw BusinessException("ipo_has_ended")
                    }
                }

                // 验证申购数量
                val applyNums = req.applyNums ?: throw BusinessException("missing_key_parameter")

                // 获取最小申购数量配置
                val minNum = appConfigService.getValueOrDefault(AppConfigCode.IPO_MIN_NUM)?.toIntOrNull() ?: 1
                if (applyNums < minNum) {
                    val errorMsg = i18nUtil.getMessage("ipo_min_num_limit", minNum.toString(), minNum)
                    throw BusinessException(errorMsg)
                }

                // 获取最大申购数量配置
                val maxNum = appConfigService.getValueOrDefault(AppConfigCode.IPO_MAX_NUM)?.toIntOrNull() ?: 10000
                if (applyNums > maxNum) {
                    val errorMsg = i18nUtil.getMessage("ipo_max_num_limit", maxNum.toString(), maxNum)
                    throw BusinessException(errorMsg)
                }

                val subscription = StockSubscription()
                subscription.orderNo = GeneratorIdUtil.generateId()
                subscription.userId = userId
                subscription.topUserId = appUser.topUserId
                subscription.applyNums = applyNums.toBigDecimal()
                subscription.buyPrice = ipo.price ?: BigDecimal.ZERO
                subscription.status = 1
                subscription.createTime = LocalDateTime.now()
                subscription.name = ipo.name
                subscription.type = ipo.type
                subscription.submitTime = LocalDateTime.now()
                subscription.allotmentTime = LocalDateTime.now()
                subscription.stockType = ipo.country

                this.save(subscription)
                R.success()
            }
        } catch (e: Exception) {
            logger.error(e) { "IPO申购异常 " }
            throw BusinessException("fail")
        }
    }

    override fun history(userId: Long): R<Any> {
        val list = this.list(
            KtQueryWrapper(StockSubscription())
                .eq(StockSubscription::userId, userId)
                .orderByDesc(StockSubscription::id)
        )
        return R.success(list)
    }

    override fun update(req: IpoUpdateRequest, userId: Long): R<Any> {
        try {
            val subscriptionId = req.id ?: throw BusinessException("missing_key_parameter")
            
            val key = RedisKeys.IPO_APPLY_LOCK_KEY + subscriptionId
            return RedisLockService.lockTransaction(key) block@{
                
                // 查询申购记录
                val subscription = this.getOne(KtQueryWrapper(StockSubscription())
                    .eq(StockSubscription::userId, userId)
                    .eq(StockSubscription::id, subscriptionId)
                )?: throw BusinessException("ipo_subscription_not_found")
                
                // 检查状态，只有状态为1（已认购）时才能修改
                if (subscription.status != 1) {
                    throw BusinessException("ipo_subscription_cannot_modify")
                }
                
                // 验证申购数量
                val applyNums = req.applyNums ?: throw BusinessException("missing_key_parameter")
                
                // 获取最小申购数量配置
                val minNum = appConfigService.getValueOrDefault(AppConfigCode.IPO_MIN_NUM)?.toIntOrNull() ?: 1
                if (applyNums < minNum) {
                    val errorMsg = i18nUtil.getMessage("ipo_min_num_limit", minNum.toString(), minNum)
                    throw BusinessException(errorMsg)
                }
                
                // 获取最大申购数量配置
                val maxNum = appConfigService.getValueOrDefault(AppConfigCode.IPO_MAX_NUM)?.toIntOrNull() ?: 10000
                if (applyNums > maxNum) {
                    val errorMsg = i18nUtil.getMessage("ipo_max_num_limit", maxNum.toString(), maxNum)
                    throw BusinessException(errorMsg)
                }
                
                // 更新申购数量
                subscription.applyNums = applyNums.toBigDecimal()
                this.updateById(subscription)
                
                R.success()
            }
        } catch (e: Exception) {
            logger.error(e) { "IPO修改申购异常 " }
            throw BusinessException("fail")
        }
    }


}
