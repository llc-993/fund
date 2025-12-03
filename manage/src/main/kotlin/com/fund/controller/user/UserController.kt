package com.fund.controller.user

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.Constants
import com.fund.common.Constants.MARKET_COIN_MAP
import com.fund.common.dto.Label
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.cash.GoldChangePageReq
import com.fund.modules.cash.WalletAdminChangeReq
import com.fund.modules.stock.service.UserPositionService
import com.fund.modules.sys.service.SysOptLogService
import com.fund.modules.user.AddAppUserReq
import com.fund.modules.user.AdminEditAppUserReq
import com.fund.modules.user.AdminUserQueryReq
import com.fund.modules.user.model.AppUser
import com.fund.modules.user.service.AppUserService
import com.fund.modules.user.vo.AdminUserVo
import com.fund.modules.wallet.enum.GoldChangeEnum
import com.fund.modules.wallet.model.AppUserCashInOrder
import com.fund.modules.wallet.model.AppUserWalletV2
import com.fund.modules.wallet.model.AppWalletOperationLog
import com.fund.modules.wallet.service.AppUserCashInOrderService
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.modules.wallet.service.AppWalletOperationLogService
import com.fund.utils.GeneratorIdUtil.generateId
import com.fund.utils.RedisLockService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDateTime
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "管理员查询用户数据")
class UserController(
    private val userService: AppUserService,
    private val userPositionService: UserPositionService,
    private val walletV2Service: AppUserWalletV2Service,
    private val cashInOrderService: AppUserCashInOrderService,
    private val optLogService: SysOptLogService,
    private val appWalletOperationLogService: AppWalletOperationLogService
) {

    private val log = KotlinLogging.logger {}

    @SaCheckLogin
    @GetMapping("/page")
    fun userPage(
        @SwaggerRequestBody(
            description = "查询用户信息",
            required = true
        ) req: AdminUserQueryReq
    ): R<Any> {
        log.info("参数：${JSON.toJSONString(req)}")
        val page = Page<AppUser>(req.pageNum, req.pageSize)

        val page1 = userService.page(
            page, KtQueryWrapper(AppUser())
                .eq(StrUtil.isNotBlank(req.userName), AppUser::userName, req.userName)
                .eq(StrUtil.isNotBlank(req.userAccount), AppUser::userAccount, req.userAccount)
                .eq(StrUtil.isNotBlank(req.mobilePhone), AppUser::mobilePhone, req.mobilePhone)
                .orderByDesc(AppUser::id)
        )

        // 获取所有用户ID列表
        val userIds = page1.records.mapNotNull { it.id }.distinct()

        // 如果有用户记录，则获取每个用户的盈亏总和
        val voList: MutableList<AdminUserVo> = mutableListOf()
        if (userIds.isNotEmpty()) {
            val userProfitMap = userPositionService.getProfitAndLoseByUser(userIds)

            for (user in page1.records) {
                val userVo = AdminUserVo()
                BeanUtil.copyProperties(user, userVo)

                userVo.profitAndLose = userProfitMap[user.id] ?: BigDecimal.ZERO

                userVo.wallet = walletV2Service.list(
                    KtQueryWrapper(AppUserWalletV2())
                        .eq(AppUserWalletV2::userId, user.id)
                )

                voList.add(userVo)
            }
        }
        val page2 = Page<AdminUserVo>(page1.current, page1.size, page1.total)
        page2.records = voList
        return R.success(page2)
    }


    @PostMapping("/adminBalanceChange")
    @Operation(summary = "后台上下分")
    fun adminChange(@RequestBody @Validated req: WalletAdminChangeReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        RedisLockService.transaction {
            when (req.type) {
                // 上分
                1 -> {
                    // 生成订单
                    val appUser = userService.getById(req.userId) ?: throw BusinessException("用户不存在")
                    val order = AppUserCashInOrder()
                    order.userId = appUser.id
                    order.userGroup = appUser.userGroup
                    order.userAccount = appUser.userAccount
                    order.mobilePhone = appUser.mobilePhone
                    order.topUserId = appUser.topUserId
                    order.ip = "system"
                    order.orderNo = "ci" + generateId()
                    order.applyTime = LocalDateTime.now()
                    order.applyAmount = req.amount
                    // 订单类型   1待处理 2已锁定 3  已取消 4 已拒绝 5 已成功
                    order.cashStatus = 5
                    order.depositCode = req.currencyCode
                    order.remitTime = LocalDateTime.now()
                    cashInOrderService.save(order)
                    // 加款
                    walletV2Service.addAvailableBalance(
                        appUser.id!!, 0, req.currencyCode!!,
                        req.amount!!, GoldChangeEnum.ADMIN_CHANGE_ADD, """
                            管理员:$adminId,
                            用户名: ${appUser.userAccount}, 
                            操作: 后台上分, 
                            备注: ${req.remark},
                            金额为: ${req.amount}
                        """.trimIndent()
                    )

                }
                // 下分
                -1 -> {
                    val appUser = userService.getById(req.userId) ?: throw BusinessException("用户不存在")
                    /*  walletService.forceBalanceChange(
                          ChangeReq.build(req.userId!!, GoldChangeEnum.ADMIN_CHANGE_SUB) {
                              balance = req.amount?.negate()
                              remark = """
                                  管理员: ${adminId}
                                  用户id: ${appUser.id},
                                  用户名: ${appUser.userAccount},
                                  操作: 后台下分,
                                  金额为: ${req.amount}
                              """.trimIndent()
                          }
                      )*/
                    walletV2Service.subtractAvailableBalance(
                        appUser.id!!, 0, req.currencyCode!!,
                        req.amount!!, GoldChangeEnum.ADMIN_CHANGE_SUB, """
                        管理员: ${adminId} ,
                        用户id: ${appUser.id}, 
                        用户名: ${appUser.userAccount}, 
                        操作: 后台下分, 
                        金额为: ${req.amount}
                    """.trimIndent()
                    )
                }

                else -> Unit
            }

        }

        optLogService.addLog(adminId, "后台上下分", JSON.toJSONString(req))
        return R.success()
    }

    @GetMapping("/gc/type")
    @Operation(summary = "账变记录类型")
    fun goldChangeType(): R<List<Label<Int, String>>> {
        return R.success(GoldChangeEnum.toLabel())
    }

    @GetMapping("/gc/page")
    @Operation(summary = "账变信息分页")
    fun goldChangePage(req: GoldChangePageReq): R<Page<AppWalletOperationLog>> {
        val p: Page<AppWalletOperationLog> = Page(req.pageNum, req.pageSize)
        val page: Page<AppWalletOperationLog> = appWalletOperationLogService.page(
            p,
            KtQueryWrapper(AppWalletOperationLog())
                .eq(req.userId != null, AppWalletOperationLog::userId, req.userId)
                .eq(req.operationType != null, AppWalletOperationLog::operationType, req.operationType)
                .gt(req.startTime != null, AppWalletOperationLog::createTime, req.startTime)
                .le(req.endTime != null, AppWalletOperationLog::createTime, req.endTime)
                .orderByDesc(AppWalletOperationLog::id, AppWalletOperationLog::createTime)
        )
        return R.success(page)
    }

    @PostMapping("/edit")
    @Operation(
        summary = "编辑会员信息",
        description = "修改用户的手机号、登录密码、交易密码、会员等级、是否假人 正常 0 假人 1、是否冻结、是否允许交易、是否允许提现"
    )
    fun edit(@RequestBody @Validated req: AdminEditAppUserReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        userService.adminEditAppUser(req) {
            if (it.isFrozen == true) {
                StpUtil.logout(req.userId)
            }
            if (it.levelWeights != null) {
                // todo 暂时没有这个需求逻辑。
            }
        }
        optLogService.addLog(adminId, "编辑会员信息", JSON.toJSONString(req))
        return R.success()
    }

    @PostMapping("/add")
    @Operation(summary = "添加会员")
    fun add(@RequestBody @Validated req: AddAppUserReq): R<Unit> {
        val adminId = StpUtil.getLoginIdAsLong()
        userService.adminAddAppUser(req, {
            // todo 会员等级
        }) {
            for (s in Constants.MARKET_COIN_MAP.values) {
                walletV2Service.createWallet(it.id!!, it.topUserId, 0, s)
            }
        }
        optLogService.addLog(adminId, "添加会员", JSON.toJSONString(req))
        return R.success()
    }

    @Operation(summary = "支持的币种")
    @GetMapping("supportCoin")
    fun supportCoin(): R<Collection<String>> {
        return R.success(MARKET_COIN_MAP.values)
    }

    @Operation(summary = "kyc列表", description = "需要审核的kyc信息列表")
    @GetMapping("kycList")
    fun kycList(): R<Any> {
        val appUsers = userService.list(
            KtQueryWrapper(AppUser())
                .eq(AppUser::kycStatus, 1)
                .orderByAsc(AppUser::id)
        )
        return R.success(appUsers)
    }

    @Operation(summary = "kyc审核", description = "kyc状态,kycStatus（0:未提交，1:已经提交、待审核，2:审核通过，3:审核失败）")
    @PostMapping("reviewKyc")
    fun reviewKyc( @RequestParam(required = true, name = "id" )id: Long, @RequestParam(required = true, name = "kycStatus" ) kycStatus: Int): R<Any> {
        val user = userService.getById(id) ?: return R.error("找不到该用户")
        if (user.kycStatus != 1) {
            return R.error("该用户的kyc 未提交、审核失败、审核通过")
        }
        user.kycStatus = kycStatus
        userService.updateById(user)

        return R.success()
    }

}