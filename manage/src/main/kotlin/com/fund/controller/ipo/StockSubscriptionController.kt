package com.fund.controller.ipo

import cn.dev33.satoken.stp.StpUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.R
import com.fund.modules.ipo.AdminSubscriptionQueryRequest
import com.fund.modules.ipo.SubscriptionConversionRequest
import com.fund.modules.ipo.model.Ipo
import com.fund.modules.ipo.model.StockSubscription
import com.fund.modules.ipo.service.IpoService
import com.fund.modules.ipo.service.StockSubscriptionService
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

@Tag(name = "IPO申购管理", description = "IPO申购列表查询、申购转持仓等功能接口")
@RestController
@RequestMapping("/subscription")
class StockSubscriptionController(
    private val stockSubscriptionService: StockSubscriptionService,
    private val appUserWalletV2Service: AppUserWalletV2Service,
    private val userPositionService: UserPositionService,
    private val ipoService: IpoService,
    private val stockService: StockService,
    private val appUserService: AppUserService,
    private val optLogService: SysOptLogService,
) {

    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "新股申购列表",
        description = "分页查询新股申购列表，支持按股票代码和名称筛选"
    )
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = [Content(schema = Schema(implementation = StockSubscription::class))])
    @GetMapping("list")
    fun list( req: AdminSubscriptionQueryRequest): R<Any> {
        val page:Page<StockSubscription> = Page(req.pageNum, req.pageSize)

        val page1 = this.stockSubscriptionService.page(
            page, KtQueryWrapper(StockSubscription())
                .eq(StringUtils.isNotBlank(req.symbol), StockSubscription::symbol, req.symbol)
                .eq(StringUtils.isNotBlank(req.name), StockSubscription::name, req.name)
                .orderByDesc(StockSubscription::id)
        )
        return R.success(page1)
    }

    @Operation(
        summary = "新股申购转化",
        description = "将中签的申购记录转化为用户持仓。业务逻辑：1.校验申购记录状态；2.计算需支付金额；3.检查钱包余额；4.创建持仓记录；5.更新申购状态。只有status=1(已认购)或status=3(已中签)的记录才能转化"
    )
    @ApiResponse(responseCode = "200", description = "转化成功")
    @PostMapping("conversion")
    fun conversion(@RequestBody req: SubscriptionConversionRequest): R<Any> {
        try {
            val adminId = StpUtil.getLoginIdAsLong()
            // 参数校验
            if (req.id == null) {
                return R.error("申购记录ID不能为空")
            }
            if (req.allotmentQuantity == null || req.allotmentQuantity!! <= BigDecimal.ZERO) {
                return R.error("中签数量必须大于0")
            }

            // 查询申购记录
            val subscription = stockSubscriptionService.getById(req.id)
                ?: return R.error("申购记录不存在")

            // 检查状态（只有已认购或已中签的才能转化）
            if (subscription.status != 1 && subscription.status != 3) {
                return R.error("当前状态不允许转化")
            }

            // 查询用户信息
            val userId = subscription.userId ?: return R.error("用户信息不存在")
            val user = appUserService.getById(userId)
                ?: return R.error("用户不存在")

            // 查询股票信息
            val stock = stockService.getOne(
                KtQueryWrapper(Stock())
                    .eq(Stock::symbol, subscription.symbol)
            ) ?: return R.error("股票信息不存在")

            // 计算需要支付的金额 = 中签数量 * 买入价格
            val buyPrice = subscription.buyPrice ?: return R.error("购买价格不存在")
            val totalAmount = req.allotmentQuantity!!.multiply(buyPrice)

            // 获取用户钱包
            val coin = appUserWalletV2Service.getCoinByStockFlag(stock.flag)
            val wallet = appUserWalletV2Service.findWalletByUserAndType(userId, 0, coin)
                ?: return R.error("钱包不存在")

            // 检查余额是否足够：如果不足，将状态改为未中签
            val availableBalance = wallet.availableBalance ?: BigDecimal.ZERO
            if (totalAmount > availableBalance) {
                logger.warn("用户余额不足，转为未中签: userId=$userId, subscriptionId=${subscription.id}, 需要=$totalAmount, 可用=$availableBalance")

                // 余额不足处理：更新申购记录为未中签状态
                subscription.status = 2  // 2 = 未中签
                subscription.allotmentQuantity = BigDecimal.ZERO  // 中签数量清零
                subscription.remarks = "余额不足，未中签"
                stockSubscriptionService.updateById(subscription)

                return R.error("用户余额不足，已将状态更新为未中签")
            }

            // 扣除钱包余额
            val deductSuccess = appUserWalletV2Service.subtractAvailableBalance(
                userId = userId,
                walletType = 0,
                currencyCode = coin,
                amount = totalAmount,
                operationType = GoldChangeEnum.IPO_CONVERSION,
                remark = "IPO转持仓: ${subscription.name}(${subscription.symbol}), 数量: ${req.allotmentQuantity}"
            )

            if (!deductSuccess) {
                return R.error("扣款失败")
            }

            val ipo = ipoService.getById(subscription.ipoId)

            // 创建用户持仓
            val userPosition = createUserPositionFromSubscription(subscription, user, stock, ipo,req.allotmentQuantity!!)

            // 保存持仓
            val saveSuccess = userPositionService.save(userPosition)
            if (!saveSuccess) {
                return R.error("创建持仓失败")
            }

            // 更新申购记录状态：转化成功后标记为已转持仓
            subscription.status = 5  // 5 = 已转持仓
            subscription.allotmentQuantity = req.allotmentQuantity  // 记录最终中签数量
            subscription.allotmentTime = LocalDateTime.now()  // 记录转化时间
            stockSubscriptionService.updateById(subscription)

            logger.info("IPO转化成功: subscriptionId=${subscription.id}, positionId=${userPosition.id}, userId=$userId, symbol=${subscription.symbol}, quantity=${req.allotmentQuantity}")
            optLogService.addLog(adminId, "IPO转化", JSON.toJSONString(req))
            return R.success(userPosition)
        } catch (e: Exception) {
            logger.error(e) { "IPO转化异常" }
            return R.error("IPO转化失败")
        }
    }

    /**
     * 从申购记录创建用户持仓
     *
     * 将IPO申购转化为正式持仓记录
     *
     * @param subscription 申购记录，包含股票代码、名称、购买价格等信息
     * @param user 用户信息
     * @param stock 股票信息
     * @param allotmentQuantity 中签数量，作为持仓数量
     * @return 创建的UserPosition对象
     *
     * 注意：
     * - IPO持仓默认为"买涨"方向
     * - 不使用杠杆（orderLever = 1）
     * - 不收取额外费用（orderFee、orderSpread等为0）
     * - 使用申购订单号作为buyOrderId
     */
    private fun createUserPositionFromSubscription(
        subscription: StockSubscription,
        user: AppUser,
        stock: Stock,
        ipo: Ipo,
        allotmentQuantity: BigDecimal
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
            orderDirection = "买涨" // IPO默认买涨
            orderNum = allotmentQuantity
            orderLever = 1 // IPO不使用杠杆
            orderTotalPrice = allotmentQuantity.multiply(subscription.buyPrice)

            // 费用（IPO转化暂不收取额外费用）
            orderFee = BigDecimal.ZERO
            orderSpread = BigDecimal.ZERO
            orderStayFee = BigDecimal.ZERO
            spreadRatePrice = BigDecimal.ZERO

            // 持仓状态
            // IPO的isLock: 0=未锁仓，1=锁仓
            // UserPosition的isLock: 1=锁定，2=不锁定
            isLock = if (ipo.isLock == 1) 1.toByte() else 2.toByte()
            orderStayDays = 0
            profitAndLose = BigDecimal.ZERO
            allProfitAndLose = BigDecimal.ZERO
            status = "1" // 持仓中
            lotUnit = lotUnit1
        }
    }

}