package com.fund.modules.stock.serviceImpl;

import cn.hutool.core.util.ObjectUtil
import com.alibaba.fastjson2.JSON
import com.fund.modules.stock.model.UserPosition;
import com.fund.modules.stock.mapper.UserPositionMapper;
import com.fund.modules.stock.service.UserPositionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fund.common.Constants
import org.springframework.context.annotation.Lazy
import com.fund.common.RedisKeys
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.stock.StockBuyRequest
import com.fund.modules.stock.service.StockService
import com.fund.utils.IpUtils
import com.fund.utils.RedisLockService
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.conf.enum.AppConfigCode
import com.fund.modules.stock.model.Stock
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.modules.wallet.service.AppUserFinanceStatsService
import com.fund.modules.user.service.AppUserService
import com.fund.modules.stock.vo.UserOrderVo
import com.fund.modules.user.model.AppUser
import com.fund.utils.I18nUtil
import com.fund.utils.GeneratorIdUtil
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.redisson.api.RateIntervalUnit
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * <p>
 * 用户持仓表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-08-23
 */
@Service
open class UserPositionServiceImpl(
    private val redissonClient: RedissonClient,
    @Lazy private val stockService: StockService,
    private val appConfigService: AppConfigService,
    private val i18nUtil: I18nUtil,
    private val appUserWalletV2Service: AppUserWalletV2Service,
    private val appUserFinanceStatsService: AppUserFinanceStatsService,
    private val appUserService: AppUserService
) : ServiceImpl<UserPositionMapper, UserPosition>(), UserPositionService {


    private val logger = KotlinLogging.logger {}

    override fun buy(req: StockBuyRequest, userId: Long, request: HttpServletRequest): R<Any> {
       // val ipAddr = IpUtils.getIpAddr()
        val key = RedisKeys.BUY_KEY + userId

        if (debounce(key, 2, 1)) {
            logger.info("购买参数: ${JSON.toJSONString(req)} , userId: $userId")
            throw BusinessException("can_only_try")
        }

        RedisLockService.lockTransaction(key) block@{
            // 验证购买数量
            req.buyNum?.let { buyNum ->
                val buyQuantity = BigDecimal.valueOf(buyNum.toLong())
                validateBuyQuantity(buyQuantity)
                logger.info("用户 $userId 购买数量 $buyNum 验证通过")
            } ?: throw BusinessException("buy_not_empty")

            val stock = stockService.getStockById(req.stockId!!.toLong())
            if (ObjectUtil.isEmpty(stock)) {
                logger.info("股票代码不存在: ${req.stockId}")
                throw BusinessException("stock_not_found")
            }
            stock.flag?.let { flag ->
                // 检查是否在交易时间内
                if (!isTradingTime(flag)) {
                    logger.info("股票 $flag 市场当前不在交易时间内，无法购买")
                    throw BusinessException("market_closed")
                }
                logger.info("股票 $flag 市场当前在交易时间内，允许购买")
            } ?: throw BusinessException("stock_not_found")

            val nowPrice: BigDecimal = stock.last!! ?: BigDecimal.ZERO

            if (nowPrice.compareTo(BigDecimal.ZERO) == 0) {
                throw BusinessException(
                    i18nUtil.getMessage("current_quote", "Quote") + "0 ,"
                            + i18nUtil.getMessage("try_again_later", "Please try again later")
                )
            }

            // 验证止盈止损价格
            validateProfitStopTarget(req.buyType, req.profitTarget, req.stopTarget, nowPrice)

            // A股涨跌停限制检查
            if (stock.flag.equals("CN")) { // 如果是A股，涨跌停后，不能下单
                if ((stock.chgPct != null) && (stock.chgPct!!.abs().compareTo(BigDecimal.TEN) >= 0)) {
                    val errorMsg = i18nUtil.getMessage(
                        "stock_daily_limit_reached",
                        "The current stock price has reached the daily limit and cannot be purchased"
                    )
                    logger.warn("A股涨跌停限制: stockId=${stock.id}, chgPct=${stock.chgPct}, flag=${stock.flag}")
                    throw BusinessException(errorMsg)
                }
            }

            // 计算购买金额
            val lotUnit = getLotUnit(stock.flag)
            val buyNum = req.buyNum ?: BigDecimal.ZERO
            val lever: Long = req.lever?.toLong() ?: 1L

            // 购买价格 = 当前价格 × 购买数量 × Lot单位
            val buyAmt = nowPrice.multiply(buyNum).multiply(BigDecimal.valueOf(lotUnit.toLong()))

            // 除以杠杆后的实际购买价格
            val buyAmtActual = buyAmt.divide(BigDecimal.valueOf(lever), 2, RoundingMode.HALF_UP)

            // 验证最小购买金额
            val minBuyAmount = getMinBuyAmount(stock.flag)
            if (buyAmtActual.compareTo(minBuyAmount) < 0) {
                val errorMsg = i18nUtil.getMessage(
                    "purchase_amount_too_low",
                    "Order placement failed, purchase amount is less than"
                ) + minBuyAmount
                logger.error("验证最小购买金额,购买金额不足: stockId=${stock.id}, buyAmtActual=$buyAmtActual, minBuyAmount=$minBuyAmount")
                throw BusinessException(errorMsg)
            }
            logger.info("购买金额计算: stockId=${stock.id}, nowPrice=$nowPrice, buyNum=$buyNum, lotUnit=$lotUnit, lever=$lever, buyAmt=$buyAmt, buyAmtActual=$buyAmtActual, minBuyAmount=$minBuyAmount")


            // 获取用户信息
            val user = appUserService.getById(userId)
            if (user == null) {
                throw BusinessException("user_not_found")
            }

            val coin = appUserWalletV2Service.getCoinByStockFlag(stock.flag)

            var wallet = appUserWalletV2Service.findWalletByUserAndType(userId, 0, coin)
            if (wallet == null) {
                wallet = appUserWalletV2Service.createWallet(userId, user.topUserId, 0, coin)

                if (wallet == null) {
                    throw BusinessException("wallet_not_found")
                }
            }

            if (buyAmtActual.compareTo(wallet.availableBalance ?: BigDecimal.ZERO) > 0) {
                throw BusinessException("insufficient_balance")
            }

            // 创建持仓订单
            val userPosition = createUserPosition(req, user, stock, nowPrice, buyAmt, lotUnit, lever)

            // 计算各种费用
            calculateFees(userPosition, buyAmt)

            // 保存订单
            val insertResult = this.save(userPosition)
            if (!insertResult) {
                throw BusinessException("order_save_failed")
            }

            // 扣除钱包余额
            val totalCost = buyAmtActual.add(userPosition.orderFee ?: BigDecimal.ZERO)
                .add(userPosition.orderSpread ?: BigDecimal.ZERO)
                .add(userPosition.spreadRatePrice ?: BigDecimal.ZERO)

            appUserWalletV2Service.subtractAvailableBalance(
                userId = userId,
                walletType = 0,
                amount = totalCost,
                operationType = "STOCK_BUY",
                remark = "股票买入: ${stock.name}(${stock.symbol}), 数量: ${req.buyNum}, 价格: $nowPrice"
            )

            // 更新资金统计
            appUserFinanceStatsService.addTradingVolumeStats(userId, buyAmt)

            // 处理止盈止损逻辑
            handleProfitStopTarget(userPosition, req, stock)

            // 更新持仓缓存
            updatePositionCache(userPosition, stock)

            logger.info("用户 $userId 成功买入股票: ${stock.name}(${stock.symbol}), 数量: ${req.buyNum}, 价格: $nowPrice, 总成本: $totalCost")

        }
        return R.success()
    }

    /**
     * 处理止盈止损逻辑
     */
    private fun handleProfitStopTarget(
        userPosition: UserPosition,
        req: StockBuyRequest,
        stock: com.fund.modules.stock.model.Stock
    ) {
        val user = appUserService.getById(userPosition.userId?.toLong() ?: 0L) ?: return

        // 创建基础订单VO
        val vo = UserOrderVo().apply {
            id = userPosition.id?.toString()
            userId = user.id?.toString()
            stockCode = stock.symbol
            stockType = stock.flag
            buyType = if (req.buyType?.toInt() == 0) "1" else "2" // 1-买涨 2-买跌
        }

        // 处理止盈
        req.profitTarget?.let { profitTarget ->
            if (profitTarget.compareTo(BigDecimal.ZERO) > 0) {
                vo.amount = profitTarget
                vo.dataType = "2" // 2-止盈平仓

                val key = String.format(RedisKeys.CHECK_ORDER_KEY, stock.flag + stock.symbol)
                val hKey = "${userPosition.id}2"

                // 使用Redisson存储到Redis
                val cacheMap = redissonClient.getMap<String, String>(key)
                cacheMap.put(hKey, JSON.toJSONString(vo))

                logger.info("设置止盈: 用户=${user.id}, 股票=${stock.symbol}, 止盈价=$profitTarget")
            }
        }

        // 处理止损
        req.stopTarget?.let { stopTarget ->
            if (stopTarget.compareTo(BigDecimal.ZERO) > 0) {
                vo.amount = stopTarget
                vo.dataType = "3" // 3-止损平仓

                val key = String.format(RedisKeys.CHECK_ORDER_KEY, stock.flag + stock.symbol)
                val hKey = "${userPosition.id}3"

                // 使用Redisson存储到Redis
                val cacheMap = redissonClient.getMap<String, String>(key)
                cacheMap.put(hKey, JSON.toJSONString(vo))

                logger.info("设置止损: 用户=${user.id}, 股票=${stock.symbol}, 止损价=$stopTarget")
            }
        }
    }

    /**
     * 更新持仓缓存
     */
    override fun updatePositionCache(
        userPosition: UserPosition,
        stock: Stock
    ) {
        try {
            // 1. 更新用户ID集合缓存（用于快速查找哪些用户持仓了该股票）
            val userSetKey = String.format(RedisKeys.CHECK_USER_POSITION_KEY, stock.flag + stock.symbol)
            val userSet = redissonClient.getSet<String>(userSetKey)
            userSet.add(userPosition.userId?.toString() ?: "")

            // 2. 存储完整的用户持仓对象到缓存
            val positionCacheKey = String.format(RedisKeys.USER_POSITION_CACHE_KEY, userPosition.userId)
            val positionBucket = redissonClient.getBucket<String>(positionCacheKey)

            // 将用户持仓对象序列化为JSON存储
            val positionJson = JSON.toJSONString(userPosition)
            positionBucket.set(positionJson, 24, java.util.concurrent.TimeUnit.HOURS) // 设置24小时过期

            // 3. 为每个股票创建用户持仓映射（股票代码 -> 用户持仓ID列表）
            val stockPositionKey = "stock_positions:${stock.flag}${stock.symbol}"
            val stockPositionSet = redissonClient.getSet<String>(stockPositionKey)
            stockPositionSet.add(userPosition.id?.toString() ?: "")

            logger.info("更新持仓缓存完成: 股票=${stock.symbol}, 用户=${userPosition.userId}, 持仓ID=${userPosition.id}")

        } catch (e: Exception) {
            logger.error(e) { "更新持仓缓存失败: 股票=${stock.symbol}, 用户=${userPosition.userId}" }
        }
    }

    /**
     * 清理持仓缓存
     */
    override fun clearPositionCache(
        userPosition: UserPosition,
        stock: Stock
    ) {
        try {
            // 1. 从用户ID集合中移除
            val userSetKey = String.format(RedisKeys.CHECK_USER_POSITION_KEY, stock.flag + stock.symbol)
            val userSet = redissonClient.getSet<String>(userSetKey)
            userSet.remove(userPosition.userId?.toString() ?: "")

            // 2. 删除用户持仓对象缓存
            val positionCacheKey = String.format(RedisKeys.USER_POSITION_CACHE_KEY, userPosition.userId)
            val positionBucket = redissonClient.getBucket<String>(positionCacheKey)
            positionBucket.delete()

            // 3. 从股票持仓映射中移除
            val stockPositionKey = "stock_positions:${stock.flag}${stock.symbol}"
            val stockPositionSet = redissonClient.getSet<String>(stockPositionKey)
            stockPositionSet.remove(userPosition.id?.toString() ?: "")

            logger.info("清理持仓缓存完成: 股票=${stock.symbol}, 用户=${userPosition.userId}, 持仓ID=${userPosition.id}")

        } catch (e: Exception) {
            logger.error(e) { "清理持仓缓存失败: 股票=${stock.symbol}, 用户=${userPosition.userId}" }
        }
    }

    /**
     * 创建用户持仓订单
     */
    private fun createUserPosition(
        req: StockBuyRequest,
        user: AppUser,
        stock: Stock,
        nowPrice: BigDecimal,
        buyAmt: BigDecimal,
        lotUnit1: Int,
        lever: Long
    ): UserPosition {
        val userPosition = UserPosition().apply {
            // 基础信息
            marginAdd = BigDecimal.ZERO
            positionType = 0 // 默认持仓类型
            positionSn = GeneratorIdUtil.generateId()
            userId = user.id?.toInt()
            nickName = user.userName
            agentId = user.topUserId?.toInt()

            // 股票信息
            stockCode = stock.symbol
            stockType = stock.flag
            stockName = stock.name

            // 订单信息
            buyOrderId = GeneratorIdUtil.generateId()
            buyOrderTime = LocalDateTime.now()
            buyOrderPrice = nowPrice
            orderDirection = if (req.buyType?.toInt() == 0) "买涨" else "买跌"
            orderNum = req.buyNum
            orderLever = lever.toInt()
            orderTotalPrice = buyAmt

            // 止盈止损
            req.profitTarget?.let {
                if (it.compareTo(BigDecimal.ZERO) > 0) {
                    profitTargetPrice = it
                }
            }
            req.stopTarget?.let {
                if (it.compareTo(BigDecimal.ZERO) > 0) {
                    stopTargetPrice = it
                }
            }

            // 其他设置
            isLock = 0
            orderStayDays = 0
            profitAndLose = BigDecimal.ZERO
            allProfitAndLose = BigDecimal.ZERO
            status = "1" // 持仓中
            lotUnit = lotUnit1
        }

        return userPosition
    }

    /**
     * 计算各种费用
     */
    private fun calculateFees(userPosition: UserPosition, buyAmt: BigDecimal) {
        // 获取配置信息
        val buyFeeRate = appConfigService.getValueOrDefault(AppConfigCode.BUY_FEE_RATE)?.toBigDecimal()
        val stayFeeRate = appConfigService.getValueOrDefault(AppConfigCode.STAY_FEE_RATE)?.toBigDecimal()
        val dutyFeeRate = appConfigService.getValueOrDefault(AppConfigCode.DUTY_FEE_RATE)?.toBigDecimal()

        // 手续费 = 配资金额 * 手续费率
        val buyFeeAmt = buyAmt.multiply(buyFeeRate).setScale(2, RoundingMode.HALF_UP)
        userPosition.orderFee = buyFeeAmt
        logger.info("用户购买手续费（配资后总资金 * 百分比） = {}", buyFeeAmt)

        // 印花税 = 配资金额 * 印花税费率
        val buyYhsAmt = buyAmt.multiply(dutyFeeRate).setScale(2, RoundingMode.HALF_UP)
        userPosition.orderSpread = buyYhsAmt
        logger.info("用户购买印花税（配资后总资金 * 百分比） = {}", buyYhsAmt)

        // 留仓费 = 配资金额 * 留仓费率 * 留仓天数
        val stayFee = buyAmt.multiply(stayFeeRate)
        val allStayFee = stayFee.multiply(BigDecimal.valueOf(userPosition.orderStayDays?.toLong() ?: 0))
        userPosition.orderStayFee = allStayFee

        // 点差费用（暂时设为0，后续可根据具体业务逻辑计算）
        userPosition.spreadRatePrice = BigDecimal.ZERO
        logger.info("用户购买点差费 = {}", userPosition.spreadRatePrice)
    }


    /**
     * 根据股票市场标志判断是否在交易时间内
     * @param stockFlag 股票市场标志 (US, CN, IN, DE)
     * @return 如果在交易时间内返回true，否则返回false
     */
    fun isTradingTime(stockFlag: String): Boolean {
        return try {
            val currentTime = ZonedDateTime.now()

            when (stockFlag.uppercase()) {
                "US" -> isMarketTradingTime(
                    currentTime,
                    appConfigService.getValueOrDefault(AppConfigCode.US_TIMEZONE) ?: "America/New_York",
                    appConfigService.getValueOrDefault(AppConfigCode.US_MORNING_OPEN) ?: "09:30",
                    appConfigService.getValueOrDefault(AppConfigCode.US_AFTERNOON_OPEN) ?: "13:00",
                    appConfigService.getValueOrDefault(AppConfigCode.US_MORNING_CLOSE) ?: "12:00",
                    appConfigService.getValueOrDefault(AppConfigCode.US_AFTERNOON_CLOSE) ?: "16:00"
                )

                "CN" -> isMarketTradingTime(
                    currentTime,
                    appConfigService.getValueOrDefault(AppConfigCode.CN_TIMEZONE) ?: "Asia/Shanghai",
                    appConfigService.getValueOrDefault(AppConfigCode.CN_MORNING_OPEN) ?: "09:30",
                    appConfigService.getValueOrDefault(AppConfigCode.CN_AFTERNOON_OPEN) ?: "13:00",
                    appConfigService.getValueOrDefault(AppConfigCode.CN_MORNING_CLOSE) ?: "11:30",
                    appConfigService.getValueOrDefault(AppConfigCode.CN_AFTERNOON_CLOSE) ?: "15:00"
                )

                "IN" -> isMarketTradingTime(
                    currentTime,
                    appConfigService.getValueOrDefault(AppConfigCode.IN_TIMEZONE) ?: "Asia/Kolkata",
                    appConfigService.getValueOrDefault(AppConfigCode.IN_MORNING_OPEN) ?: "09:15",
                    appConfigService.getValueOrDefault(AppConfigCode.IN_AFTERNOON_OPEN) ?: "14:00",
                    appConfigService.getValueOrDefault(AppConfigCode.IN_MORNING_CLOSE) ?: "11:30",
                    appConfigService.getValueOrDefault(AppConfigCode.IN_AFTERNOON_CLOSE) ?: "15:30"
                )

                "DE" -> isMarketTradingTime(
                    currentTime,
                    appConfigService.getValueOrDefault(AppConfigCode.DE_TIMEZONE) ?: "Europe/Berlin",
                    appConfigService.getValueOrDefault(AppConfigCode.DE_MORNING_OPEN) ?: "09:00",
                    appConfigService.getValueOrDefault(AppConfigCode.DE_AFTERNOON_OPEN) ?: "13:00",
                    appConfigService.getValueOrDefault(AppConfigCode.DE_MORNING_CLOSE) ?: "12:00",
                    appConfigService.getValueOrDefault(AppConfigCode.DE_AFTERNOON_CLOSE) ?: "17:30"
                )

                else -> {
                    logger.warn("未知的股票市场标志: $stockFlag")
                    false
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "判断交易时间时发生错误: $stockFlag" }
            false
        }
    }

    /**
     * 判断指定市场是否在交易时间内
     * @param currentTime 当前时间
     * @param timezone 时区
     * @param morningOpen 上午开盘时间
     * @param afternoonOpen 下午开盘时间
     * @param morningClose 上午收盘时间
     * @param afternoonClose 下午收盘时间
     * @return 是否在交易时间内
     */
    private fun isMarketTradingTime(
        currentTime: ZonedDateTime,
        timezone: String, morningOpen: String, afternoonOpen: String, morningClose: String, afternoonClose: String
    ): Boolean {
        try {
            // 转换到指定时区
            val marketTime = currentTime.withZoneSameInstant(ZoneId.of(timezone))
            val currentTimeStr = marketTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            val currentTimeLocal = LocalTime.parse(currentTimeStr)

            // 解析交易时间
            val morningOpenTime = LocalTime.parse(morningOpen)
            val afternoonOpenTime = LocalTime.parse(afternoonOpen)
            val morningCloseTime = LocalTime.parse(morningClose)
            val afternoonCloseTime = LocalTime.parse(afternoonClose)

            // 判断是否在上午交易时间 (开盘时间 <= 当前时间 <= 上午收盘时间)
            val isMorningTrading = currentTimeLocal.isAfter(morningOpenTime) &&
                    currentTimeLocal.isBefore(morningCloseTime) ||
                    currentTimeLocal == morningOpenTime ||
                    currentTimeLocal == morningCloseTime

            // 判断是否在下午交易时间 (下午开盘时间 <= 当前时间 <= 下午收盘时间)
            val isAfternoonTrading = currentTimeLocal.isAfter(afternoonOpenTime) &&
                    currentTimeLocal.isBefore(afternoonCloseTime) ||
                    currentTimeLocal == afternoonOpenTime ||
                    currentTimeLocal == afternoonCloseTime

            logger.debug("市场时区: $timezone, 当前时间: $currentTimeStr, 上午交易: $isMorningTrading, 下午交易: $isAfternoonTrading")

            return isMorningTrading || isAfternoonTrading
        } catch (e: Exception) {
            logger.error(e) { "解析交易时间配置时发生错误: timezone=$timezone, morningOpen=$morningOpen" }
            return false
        }
    }

    /**
     * 获取指定市场的最小购买金额
     * @param marketFlag 市场标志 (US, CN, IN, DE)
     * @return 最小购买金额
     */
    fun getMinBuyAmount(marketFlag: String?): BigDecimal {
        return try {
            val minAmountStr = when (marketFlag?.uppercase()) {
                "US" -> appConfigService.getValueOrDefault(AppConfigCode.US_MIN_BUY_AMOUNT) ?: "10"
                "CN" -> appConfigService.getValueOrDefault(AppConfigCode.CN_MIN_BUY_AMOUNT) ?: "100"
                "IN" -> appConfigService.getValueOrDefault(AppConfigCode.IN_MIN_BUY_AMOUNT) ?: "10"
                "DE" -> appConfigService.getValueOrDefault(AppConfigCode.DE_MIN_BUY_AMOUNT) ?: "10"
                else -> {
                    logger.warn("未知的市场标志: $marketFlag，使用默认最小购买金额: 10")
                    "10"
                }
            }
            BigDecimal(minAmountStr)
        } catch (e: NumberFormatException) {
            logger.error(e) { "解析最小购买金额配置时发生数字格式错误: marketFlag=$marketFlag" }
            BigDecimal.TEN
        } catch (e: Exception) {
            logger.error(e) { "获取最小购买金额时发生未知错误: marketFlag=$marketFlag" }
            BigDecimal.TEN
        }
    }

    /**
     * 获取指定市场的Lot单位
     * @param marketFlag 市场标志 (US, CN, IN, DE)
     * @return Lot单位
     */
    fun getLotUnit(marketFlag: String?): Int {
        return try {
            when (marketFlag?.uppercase()) {
                "US" -> appConfigService.getValueOrDefault(AppConfigCode.US_LOT_UNIT)?.toInt() ?: 1
                "CN" -> appConfigService.getValueOrDefault(AppConfigCode.CN_LOT_UNIT)?.toInt() ?: 1
                "IN" -> appConfigService.getValueOrDefault(AppConfigCode.IN_LOT_UNIT)?.toInt() ?: 1
                "DE" -> appConfigService.getValueOrDefault(AppConfigCode.DE_LOT_UNIT)?.toInt() ?: 1
                else -> {
                    logger.warn("未知的市场标志: $marketFlag，使用默认Lot单位: 1")
                    1
                }
            }
        } catch (e: NumberFormatException) {
            logger.error(e) { "解析Lot单位配置时发生数字格式错误: marketFlag=$marketFlag" }
            1
        } catch (e: Exception) {
            logger.error(e) { "获取Lot单位时发生未知错误: marketFlag=$marketFlag" }
            1
        }
    }

    /**
     * 验证止盈止损价格
     * @param buyType 买卖类型 (0=买入, 1=卖出)
     * @param profitTarget 止盈价格
     * @param stopTarget 止损价格
     * @param nowPrice 当前价格
     */
    fun validateProfitStopTarget(
        buyType: Int?,
        profitTarget: BigDecimal?,
        stopTarget: BigDecimal?,
        nowPrice: BigDecimal
    ) {
        if (buyType == null) {
            return // 如果没有指定买卖类型，跳过验证
        }

        if (buyType == 0) { // 买入
            // 买入时止盈价格不能比当前报价低
            if (profitTarget != null && profitTarget.compareTo(nowPrice) < 0) {
                val errorMsg = i18nUtil.getMessage(
                    "buy_profit_target_too_low",
                    "When buying, the take profit price cannot be lower than the current quotation"
                ) +
                        i18nUtil.getMessage("current_quote", "Quote") + nowPrice
                logger.warn("买入止盈价格验证失败: profitTarget=$profitTarget, nowPrice=$nowPrice")
                throw BusinessException(errorMsg)
            }

            // 买入时止损价格不能比当前报价高
            if (stopTarget != null && stopTarget.compareTo(nowPrice) > 0) {
                val errorMsg = i18nUtil.getMessage(
                    "buy_stop_loss_too_high",
                    "When buying, the stop loss price cannot be higher than the current quotation"
                ) +
                        i18nUtil.getMessage("current_quote", "Quote") + nowPrice
                logger.warn("买入止损价格验证失败: stopTarget=$stopTarget, nowPrice=$nowPrice")
                throw BusinessException(errorMsg)
            }
        } else { // 卖出
            // 卖出时止盈价格不能比当前报价高
            if (profitTarget != null && profitTarget.compareTo(nowPrice) > 0) {
                val errorMsg = i18nUtil.getMessage(
                    "sell_profit_target_too_high",
                    "When selling, the take profit price cannot be higher than the current quotation"
                ) +
                        i18nUtil.getMessage("current_quote", "Quote") + nowPrice
                logger.warn("卖出止盈价格验证失败: profitTarget=$profitTarget, nowPrice=$nowPrice")
                throw BusinessException(errorMsg)
            }

            // 卖出时止损价格不能比当前报价低
            if (stopTarget != null && stopTarget.compareTo(nowPrice) < 0) {
                val errorMsg = i18nUtil.getMessage(
                    "sell_stop_loss_too_low",
                    "When selling, the stop loss price cannot be lower than the current quotation"
                ) +
                        i18nUtil.getMessage("current_quote", "Quote") + nowPrice
                logger.warn("卖出止损价格验证失败: stopTarget=$stopTarget, nowPrice=$nowPrice")
                throw BusinessException(errorMsg)
            }
        }

        logger.info("止盈止损价格验证通过: buyType=$buyType, profitTarget=$profitTarget, stopTarget=$stopTarget, nowPrice=$nowPrice")
    }

    /**
     * 验证股票购买数量
     * @param buyQuantity 购买数量
     * @return 验证结果，true表示通过验证
     */
    fun validateBuyQuantity(buyQuantity: BigDecimal): Boolean {
        return try {
            // 检查购买数量是否为null或非正数
            if (buyQuantity == null || buyQuantity <= BigDecimal.ZERO) {
                logger.warn("购买数量无效: $buyQuantity")
                throw BusinessException("buy_not_empty")
            }

            // 获取配置的最小和最大购买数量
            val minQuantity =
                appConfigService.getValueOrDefault(AppConfigCode.BUY_MIN_NUM)?.toBigDecimal() ?: BigDecimal.ONE
            val maxQuantity =
                appConfigService.getValueOrDefault(AppConfigCode.BUY_MAX_NUM)?.toBigDecimal() ?: BigDecimal.valueOf(
                    999999
                )

            // 验证配置值的有效性
            if (minQuantity <= BigDecimal.ZERO || maxQuantity <= BigDecimal.ZERO) {
                logger.error("购买数量配置无效: minQuantity=$minQuantity, maxQuantity=$maxQuantity")
                throw BusinessException("config_error")
            }

            if (minQuantity > maxQuantity) {
                logger.error("购买数量配置错误: 最小数量 $minQuantity 大于最大数量 $maxQuantity")
                throw BusinessException("config_error")
            }

            // 使用 BigDecimal 的 compareTo 方法进行比较
            when {
                buyQuantity.compareTo(minQuantity) < 0 -> {
                    logger.warn("购买数量 $buyQuantity 小于最小购买数量 $minQuantity")
                    throw BusinessException("min_buy_quantity_limit")
                }

                buyQuantity.compareTo(maxQuantity) > 0 -> {
                    logger.warn("购买数量 $buyQuantity 大于最大购买数量 $maxQuantity")
                    throw BusinessException("max_buy_quantity_limit")
                }

                else -> {
                    logger.info("购买数量 $buyQuantity 验证通过，范围: $minQuantity - $maxQuantity")
                    true
                }
            }
        } catch (e: NumberFormatException) {
            logger.error(e) { "解析购买数量配置时发生数字格式错误" }
            throw BusinessException("config_error")
        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.error(e) { "验证购买数量时发生未知错误: ${e.message}" }
            throw BusinessException("validation_error")
        }
    }

    /**
     * 防抖方法
     * @param key Redis键名
     * @param expireSeconds 过期时间（秒）
     * @param maxRequests 在指定时间窗口内允许的最大请求数
     * @return 是否允许请求通过
     */
    fun debounce(key: String, expireSeconds: Long, maxRequests: Long): Boolean {
        val rateLimiter = redissonClient.getRateLimiter(key)

        // 初始化限流器：在指定时间窗口内允许指定数量的请求
        // 例如：2秒内只能1个请求，则 rate = 1, rateInterval = 2
        rateLimiter.trySetRate(
            org.redisson.api.RateType.OVERALL,
            maxRequests,
            expireSeconds,
            RateIntervalUnit.SECONDS
        )

        // 尝试获取一个令牌
        return rateLimiter.tryAcquire(1)
    }

}
