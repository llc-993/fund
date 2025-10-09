package com.fund.modules.ipo.serviceImpl;

import com.fund.modules.ipo.model.Ipo;
import com.fund.modules.ipo.mapper.IpoMapper;
import com.fund.modules.ipo.service.IpoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.conf.enum.AppConfigCode
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.ipo.IpoApplyRequest
import com.fund.modules.ipo.model.StockSubscription
import com.fund.modules.ipo.service.StockSubscriptionService
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
 * IPO信息表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-10-07
 */
@Service
open class IpoServiceImpl(
) : ServiceImpl<IpoMapper, Ipo>(), IpoService {

    private val logger = KotlinLogging.logger {}



}
