package com.fund.controller.risingFalling

import cn.dev33.satoken.stp.StpUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.modules.risingfalling.AdminRisingFallingSectorsSubscriptionQueryRequest
import com.fund.modules.risingfalling.RisingFallingSectorsConversionRequest
import com.fund.modules.risingFalling.model.RisingFallingSectors
import com.fund.modules.risingFalling.model.RisingFallingSectorsSubscription
import com.fund.modules.risingFalling.service.RisingFallingSectorsService
import com.fund.modules.risingFalling.service.RisingFallingSectorsSubscriptionService
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.model.UserPosition
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.sys.service.SysOptLogService
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import com.fund.modules.wallet.enum.GoldChangeEnum
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.GeneratorIdUtil
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 涨跌板块申购控制器
 *
 * 提供涨跌板块申购相关功能：
 * - 申购列表查询
 * - 申购转持仓（确认转化）
 *
 * 状态说明：
 * - status=1: 已申购
 * - status=2: 已取消
 * - status=3: 已确认
 * - status=4: 已转持仓
 */
@Tag(name = "涨跌板块申购", description = "涨跌板块申购查询与转持仓相关接口")
@RestController
@RequestMapping("/risingFalling/subscription")
class RisingFallingSectorsSubscriptionController(
    private val risingFallingSectorsSubscriptionService: RisingFallingSectorsSubscriptionService,
    private val risingFallingSectorsService: RisingFallingSectorsService,
    private val appUserWalletV2Service: AppUserWalletV2Service,
    private val optLogService: SysOptLogService,
    private val userPositionService: UserPositionService,
    private val stockService: StockService,
    private val appUserService: AppUserService
) {

    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "申购列表查询",
        description = "分页查询涨跌板块申购列表，支持按名称、标的、用户ID与状态过滤，按ID倒序",
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = [Content(schema = Schema(implementation = RisingFallingSectorsSubscription::class))]
    )
    @GetMapping("list")
    fun list(
        @SwaggerRequestBody(
            description = "查询条件，包含分页与过滤参数（名称、标的、用户、状态）",
            required = true
        )
         req: AdminRisingFallingSectorsSubscriptionQueryRequest
    ): R<Any> {
        val page: Page<RisingFallingSectorsSubscription> = Page(req.pageNum, req.pageSize)

        val page1 = risingFallingSectorsSubscriptionService.page(
            page, KtQueryWrapper(RisingFallingSectorsSubscription())
                .eq(StringUtils.isNotBlank(req.name), RisingFallingSectorsSubscription::name, req.name)
                .eq(StringUtils.isNotBlank(req.symbol), RisingFallingSectorsSubscription::symbol, req.symbol)
                .eq(req.userId != null, RisingFallingSectorsSubscription::userId, req.userId)
                .eq(req.status != null, RisingFallingSectorsSubscription::status, req.status)
                .orderByDesc(RisingFallingSectorsSubscription::id)
        )
        return R.success(page1)
    }

    @Operation(
        summary = "申购转持仓",
        description = "将指定的涨跌板块申购记录转化为用户持仓。仅当状态为 1(已申购) 或 3(已确认) 时允许转化",
    )
    @ApiResponse(
        responseCode = "200",
        description = "转化成功，返回创建的持仓对象",
        content = [Content(schema = Schema(implementation = R::class))]
    )
    @ApiResponse(responseCode = "400", description = "参数或业务校验失败")
    @PostMapping("conversion")
    fun conversion(
        @SwaggerRequestBody(
            description = "转化请求参数，包含申购ID与确认数量",
            required = true
        )
        @RequestBody req: RisingFallingSectorsConversionRequest
    ): R<Any> {
        try {
            val adminId = StpUtil.getLoginIdAsLong()

            // 参数校验
            if (req.id == null) {
                return R.error("申购记录ID不能为空")
            }
            if (req.confirmQuantity == null || req.confirmQuantity!! <= BigDecimal.ZERO) {
                return R.error("确认数量必须大于0")
            }

            // 查询申购记录
            val subscription = risingFallingSectorsSubscriptionService.getById(req.id)
                ?: return R.error("申购记录不存在")

            // 检查状态（只有已申购或已确认的才能转化）
            if (subscription.status != 1 && subscription.status != 3) {
                return R.error("当前状态不允许转化")
            }

            // 查询用户信息
            val userId = subscription.userId ?: return R.error("用户信息不存在")
            val user = appUserService.getById(userId)
                ?: return R.error("用户不存在")

            // 查询股票信息
            val stock = stockService.getById(subscription.stockId)
                ?: return R.error("股票信息不存在")

            // 查询涨跌板块信息
            val risingFallingSectors = risingFallingSectorsService.getById(subscription.risingFallingSectorsId?.toLong())
                ?: return R.error("涨跌板块信息不存在")

            // 计算需要支付的金额 = 确认数量 * 买入价格
            val buyPrice = subscription.buyPrice ?: return R.error("购买价格不存在")
            val totalAmount = req.confirmQuantity!!.multiply(buyPrice)

            // 获取用户钱包
            val coin = appUserWalletV2Service.getCoinByStockFlag(stock.flag)
            val wallet = appUserWalletV2Service.findWalletByUserAndType(userId, 0, coin)
                ?: return R.error("钱包不存在")

            // 检查余额是否足够：如果不足，将状态改为已取消
            val availableBalance = wallet.availableBalance ?: BigDecimal.ZERO
            if (totalAmount > availableBalance) {
                logger.warn("用户余额不足，转为已取消: userId=$userId, subscriptionId=${subscription.id}, 需要=$totalAmount, 可用=$availableBalance")

                // 余额不足处理：更新申购记录为已取消状态
                subscription.status = 2  // 2 = 已取消
                subscription.remarks = "余额不足，已取消"
                risingFallingSectorsSubscriptionService.updateById(subscription)

                return R.error("用户余额不足，已将状态更新为已取消")
            }

            // 扣除钱包余额
            val deductSuccess = appUserWalletV2Service.subtractAvailableBalance(
                userId = userId,
                walletType = 0,
                currencyCode = coin,
                amount = totalAmount,
                operationType = GoldChangeEnum.RISING_FALLING_SECTORS_CONVERSION,
                remark = "涨跌板块转持仓: ${subscription.name}, 数量: ${req.confirmQuantity}"
            )

            if (!deductSuccess) {
                return R.error("扣款失败")
            }

            // 创建用户持仓
            val userPosition = createUserPositionFromSubscription(
                subscription,
                user,
                stock,
                risingFallingSectors,
                req.confirmQuantity!!
            )

            // 保存持仓
            val saveSuccess = userPositionService.save(userPosition)
            if (!saveSuccess) {
                return R.error("创建持仓失败")
            }

            // 更新申购记录状态：转化成功后标记为已转持仓
            subscription.status = 4  // 4 = 已转持仓
            subscription.confirmTime = LocalDateTime.now()  // 记录转化时间
            risingFallingSectorsSubscriptionService.updateById(subscription)

            logger.info("涨跌板块转化成功: subscriptionId=${subscription.id}, positionId=${userPosition.id}, userId=$userId, stockId=${subscription.stockId}, quantity=${req.confirmQuantity}")

            optLogService.addLog(adminId, "涨跌板块转化", JSON.toJSONString(req))

            return R.success(userPosition)

        } catch (e: Exception) {
            logger.error(e) { "涨跌板块转化异常" }
            return R.error("涨跌板块转化失败")
        }
    }

    /**
     * 从申购记录创建用户持仓
     *
     * 将涨跌板块申购转化为正式持仓记录
     *
     * @param subscription 申购记录，包含股票信息、购买价格等
     * @param user 用户信息
     * @param stock 股票信息
     * @param risingFallingSectors 涨跌板块信息
     * @param confirmQuantity 确认数量，作为持仓数量
     * @return 创建的UserPosition对象
     *
     * 注意：
     * - 涨跌板块持仓默认为"买涨"方向
     * - 不使用杠杆（orderLever = 1）
     * - 不收取额外费用（orderFee、orderSpread等为0）
     * - 使用申购订单号作为buyOrderId
     * - 根据涨跌板块的锁定状态设置持仓锁定状态
     */
    private fun createUserPositionFromSubscription(
        subscription: RisingFallingSectorsSubscription,
        user: AppUser,
        stock: Stock,
        risingFallingSectors: RisingFallingSectors,
        confirmQuantity: BigDecimal
    ): UserPosition {
        val lotUnit1 = userPositionService.getLotUnit(stock.flag)
        return UserPosition().apply {
            // 基础信息
            marginAdd = BigDecimal.ZERO
            positionType = 0 // 默认持仓类型
            positionSn = GeneratorIdUtil.generateId()
            userId = user.id
            nickName = user.userName
            agentId = user.topUserId?.toInt()

            // 股票信息
            stockCode = stock.symbol
            stockType = stock.flag
            stockName = stock.name
            stockGid = stock.id.toString()

            // 订单信息
            buyOrderId = subscription.orderNo
            buyOrderTime = LocalDateTime.now()
            buyOrderPrice = subscription.buyPrice
            orderDirection = "买涨" // 涨跌板块默认买涨
            orderNum = confirmQuantity
            orderLever = 1 // 涨跌板块不使用杠杆
            orderTotalPrice = confirmQuantity.multiply(subscription.buyPrice)

            // 费用（涨跌板块转化暂不收取额外费用）
            orderFee = BigDecimal.ZERO
            orderSpread = BigDecimal.ZERO
            orderStayFee = BigDecimal.ZERO
            spreadRatePrice = BigDecimal.ZERO

            // 持仓状态
            // RisingFallingSectors的stockLockStatus: 1=锁定，2=不锁定
            // UserPosition的isLock: 1=锁定，2=不锁定（一致）
            isLock = risingFallingSectors.stockLockStatus?.toByte() ?: 2.toByte()
            orderStayDays = 0
            profitAndLose = BigDecimal.ZERO
            allProfitAndLose = BigDecimal.ZERO
            status = "1" // 持仓中
            lotUnit = lotUnit1
        }
    }

}
