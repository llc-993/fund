package com.fund.controller.block

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.modules.block.AdminBlockTradeSubscriptionQueryRequest
import com.fund.modules.block.BlockTradeConversionRequest
import com.fund.modules.block.model.StockBlockTrade
import com.fund.modules.block.model.StockBlockTradeSubscription
import com.fund.modules.block.service.StockBlockTradeService
import com.fund.modules.block.service.StockBlockTradeSubscriptionService
import com.fund.modules.stock.model.Stock
import com.fund.modules.stock.model.UserPosition
import com.fund.modules.stock.service.StockService
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import com.fund.modules.wallet.enum.GoldChangeEnum
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.GeneratorIdUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Tag(name = "大宗交易申购管理", description = "大宗交易申购列表查询、申购转持仓等功能接口")
@RestController
@RequestMapping("/block/subscription")
class StockBlockTradeSubscriptionController(
    private val stockBlockTradeSubscriptionService: StockBlockTradeSubscriptionService,
    private val stockBlockTradeService: StockBlockTradeService,
    private val appUserWalletV2Service: AppUserWalletV2Service,
    private val userPositionService: UserPositionService,
    private val stockService: StockService,
    private val appUserService: AppUserService
) {

    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "大宗交易申购列表",
        description = "分页查询大宗交易申购列表，支持按股票名称、用户ID、状态筛选"
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = [Content(schema = Schema(implementation = StockBlockTradeSubscription::class))]
    )
    @GetMapping("list")
    fun list(@RequestBody req: AdminBlockTradeSubscriptionQueryRequest): R<Any> {
        val page: Page<StockBlockTradeSubscription> = Page(req.pageNum, req.pageSize)

        val page1 = stockBlockTradeSubscriptionService.page(
            page, KtQueryWrapper(StockBlockTradeSubscription())
                .eq(StringUtils.isNotBlank(req.name), StockBlockTradeSubscription::name, req.name)
                .eq(req.userId != null, StockBlockTradeSubscription::userId, req.userId)
                .eq(req.status != null, StockBlockTradeSubscription::status, req.status)
                .orderByDesc(StockBlockTradeSubscription::id)
        )
        return R.success(page1)
    }

    @Operation(
        summary = "大宗交易申购转化",
        description = "将申购记录转化为用户持仓。业务逻辑：1.校验申购记录状态；2.计算需支付金额；3.检查钱包余额；4.创建持仓记录；5.更新申购状态。只有status=1(已申购)或status=3(已确认)的记录才能转化"
    )
    @ApiResponse(
        responseCode = "200",
        description = "转化成功",
        content = [Content(schema = Schema(implementation = UserPosition::class))]
    )
    @PostMapping("conversion")
    fun conversion(@RequestBody req: BlockTradeConversionRequest): R<Any> {
        try {
            // 参数校验
            if (req.id == null) {
                return R.error("申购记录ID不能为空")
            }
            if (req.confirmQuantity == null || req.confirmQuantity!! <= BigDecimal.ZERO) {
                return R.error("确认数量必须大于0")
            }

            // 查询申购记录
            val subscription = stockBlockTradeSubscriptionService.getById(req.id)
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
            val stock = stockService.getById(subscription.stockId?.toLong())
                ?: return R.error("股票信息不存在")

            // 查询大宗交易信息
            val blockTrade = stockBlockTradeService.getById(subscription.blockTradeId?.toInt())
                ?: return R.error("大宗交易信息不存在")

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
                stockBlockTradeSubscriptionService.updateById(subscription)

                return R.error("用户余额不足，已将状态更新为已取消")
            }

            // 扣除钱包余额
            val deductSuccess = appUserWalletV2Service.subtractAvailableBalance(
                userId = userId,
                walletType = 0,
                currencyCode = coin,
                amount = totalAmount,
                operationType = GoldChangeEnum.BLOCK_TRADE_CONVERSION.enumName,
                remark = "大宗交易转持仓: ${subscription.name}, 数量: ${req.confirmQuantity}"
            )

            if (!deductSuccess) {
                return R.error("扣款失败")
            }

            // 创建用户持仓
            val userPosition = createUserPositionFromSubscription(
                subscription,
                user,
                stock,
                blockTrade,
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
            stockBlockTradeSubscriptionService.updateById(subscription)

            logger.info("大宗交易转化成功: subscriptionId=${subscription.id}, positionId=${userPosition.id}, userId=$userId, stockId=${subscription.stockId}, quantity=${req.confirmQuantity}")
            return R.success(userPosition)

        } catch (e: Exception) {
            logger.error(e) { "大宗交易转化异常" }
            return R.error("大宗交易转化失败")
        }
    }

    /**
     * 从申购记录创建用户持仓
     *
     * 将大宗交易申购转化为正式持仓记录
     *
     * @param subscription 申购记录，包含股票信息、购买价格等
     * @param user 用户信息
     * @param stock 股票信息
     * @param blockTrade 大宗交易信息
     * @param confirmQuantity 确认数量，作为持仓数量
     * @return 创建的UserPosition对象
     *
     * 注意：
     * - 大宗交易持仓默认为"买涨"方向
     * - 不使用杠杆（orderLever = 1）
     * - 不收取额外费用（orderFee、orderSpread等为0）
     * - 使用申购订单号作为buyOrderId
     * - 根据大宗交易的锁定状态设置持仓锁定状态
     */
    private fun createUserPositionFromSubscription(
        subscription: StockBlockTradeSubscription,
        user: AppUser,
        stock: Stock,
        blockTrade: StockBlockTrade,
        confirmQuantity: BigDecimal
    ): UserPosition {
        val lotUnit1 = userPositionService.getLotUnit(stock.flag)
        return UserPosition().apply {
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
            buyOrderId = subscription.orderNo
            buyOrderTime = LocalDateTime.now()
            buyOrderPrice = subscription.buyPrice
            orderDirection = "买涨" // 大宗交易默认买涨
            orderNum = confirmQuantity
            orderLever = 1 // 大宗交易不使用杠杆
            orderTotalPrice = confirmQuantity.multiply(subscription.buyPrice)

            // 费用（大宗交易转化暂不收取额外费用）
            orderFee = BigDecimal.ZERO
            orderSpread = BigDecimal.ZERO
            orderStayFee = BigDecimal.ZERO
            spreadRatePrice = BigDecimal.ZERO

            // 持仓状态
            // BlockTrade的lockStatus: 1=锁定，2=不锁定
            // UserPosition的isLock: 1=锁定，2=不锁定（一致）
            isLock = blockTrade.lockStatus?.toByte() ?: 2.toByte()
            orderStayDays = 0
            profitAndLose = BigDecimal.ZERO
            allProfitAndLose = BigDecimal.ZERO
            status = "1" // 持仓中
            lotUnit = lotUnit1
        }
    }

}

