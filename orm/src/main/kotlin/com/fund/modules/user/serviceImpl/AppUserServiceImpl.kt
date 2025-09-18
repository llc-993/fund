package com.fund.modules.user.serviceImpl;

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.crypto.digest.MD5
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.fund.modules.user.model.AppUser;
import com.fund.modules.user.mapper.AppUserMapper;
import com.fund.modules.user.service.AppUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.Constants
import com.fund.common.RedisKeys
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.agent.model.AppAgentRelation
import com.fund.modules.agent.service.AppAgentRelationService
import com.fund.modules.conf.enum.AppConfigCode
import com.fund.modules.conf.service.AppConfigService
import com.fund.modules.user.UserChangePasswordRequest
import com.fund.modules.user.UserLoginRequest
import com.fund.modules.user.UserRegisterRequest
import com.fund.modules.user.vo.AppLoginInfo
import com.fund.modules.wallet.service.AppUserWalletV2Service
import com.fund.utils.IpService
import com.fund.utils.IpUtils
import com.fund.utils.RedisLockService
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service;
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@Service
open class AppUserServiceImpl(
    private val appAgentRelationService: AppAgentRelationService,
    private val appConfigService: AppConfigService,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val ipService: IpService,
    private val appUserWalletV2Service: AppUserWalletV2Service,
) : ServiceImpl<AppUserMapper, AppUser>(), AppUserService {
    private val logger = KotlinLogging.logger {}


    override fun register(
        userRegisterRequest: UserRegisterRequest,
        request: HttpServletRequest
    ): R<AppLoginInfo> {
        val clientIP: String? = IpUtils.getRealIP(request)

        val countKey = RedisKeys.LIMIT_IP_REG + clientIP
        logger.info("注册ip: {}", clientIP)
        // 同一ip一天最多能请求999次注册接口(不论成功或者失败)
        val limitReg = appConfigService.getValueOrDefault(AppConfigCode.REG_LIMIT_DAY)!!.toInt()
        val limitRequest = limitFunctionToday(countKey, limitReg)
        if (limitRequest) {
            logger.warn("key： {} 请求方法次数已达上限{}", countKey, limitReg)
            // 防刷注册
            throw BusinessException("fail")
        }
        // 比较两次密码
        if (userRegisterRequest.password != userRegisterRequest.confirmPassword) {
            // 两次密码填写不一样！
            throw BusinessException("the_two_passwords_are_different")
        }
        userRegisterRequest.username = userRegisterRequest.username?.trim()
        userRegisterRequest.mobilePhone = userRegisterRequest.mobilePhone?.trim()

        return RedisLockService.lockTransaction(RedisKeys.LOCK_REG + userRegisterRequest.username) {
            // 判断账号是否存在
            val existsAccount = this.count(
                KtQueryWrapper(AppUser())
                    .select(AppUser::id)
                    .eq(AppUser::mobilePhone, userRegisterRequest.mobilePhone)
            ) > 0

            if (existsAccount) {
                throw BusinessException("account_already_exists")
            }

            // 查询代理关系是否存在
            val ar: AppAgentRelation = appAgentRelationService.findAgentByCode(userRegisterRequest.shareCode!!)
                ?: throw BusinessException("invalid invitation code")

            // 生成用户
            val user = AppUser()
            user.topUserId = ar.topUserId
            user.userName = userRegisterRequest.username
            user.userAccount = userRegisterRequest.username
            user.mobilePhone = userRegisterRequest.mobilePhone
            user.keyword = "${user.userAccount};${user.mobilePhone}"
            user.password = MD5.create().digestHex(userRegisterRequest.password)
            user.showPassword = userRegisterRequest.password
            user.gender = -1

            // 用来判断等级
            if (StrUtil.isNotBlank(userRegisterRequest.moneyPassword)) {
                user.moneyPassword = MD5.create().digestHex(userRegisterRequest.moneyPassword)
                user.showMoneyPassword = userRegisterRequest.moneyPassword
            }


            // 头像相对路径
            user.avatar = appConfigService.getValueOrDefault(AppConfigCode.DEFAULT_AVATAR)
            user.isFrozen = false
            user.registerIp = clientIP
            user.registerTime = LocalDateTime.now()
            user.lastLoginIp = clientIP
            user.lastLoginTime = LocalDateTime.now()
            save(user)

            // 生成代理关系
            val myAr: AppAgentRelation = appAgentRelationService.createMemAgentRelation(user, ar)
            appAgentRelationService.save(myAr)

            // 更新ip区域
            val area: String? = clientIP?.let { ipService.getRealAddressByIP(it) }

            // 更新邀请码
            user.shareCode = myAr.oriShareCode
            user.keyword = "${user.userAccount};${user.mobilePhone};${user.shareCode}"
            user.registerArea = area
            updateById(user)

            StpUtil.login(user.id, true)

            //钱包资产生成
            for (s in Constants.MARKET_COIN_MAP.values) {
                appUserWalletV2Service.createWallet(user.id!!, user.topUserId, 0, s)
            }
            val info = AppLoginInfo()
            info.account = user.userAccount
            info.avatar = user.avatar
            info.token = StpUtil.getTokenValue()

            R.success(info)
        }
    }

    override fun login(req: UserLoginRequest, request: HttpServletRequest): R<Any> {
        try {
            req.userAccount = req.userAccount?.trim()

            val appUser = this.findUserByAccount(req.userAccount!!)
                ?: // 账号或密码不正确
                throw BusinessException("account_or_password_is_incorrect")
            if (MD5.create().digestHex(req.password!!)
                    .lowercase(Locale.ROOT) != appUser.password!!.lowercase(Locale.ROOT)
            ) {
                // 账号或密码不正确
                throw BusinessException("account_or_password_is_incorrect")
            }
            if (appUser.isFrozen!!) {
                throw BusinessException("account_is_abnormal")
            }

            val clientIP = IpUtils.getIpAddr()
            // 更新登陆ip
            update(
                KtUpdateWrapper(AppUser())
                    .eq(AppUser::id, appUser.id)
                    .set(AppUser::lastLoginIp, clientIP)
                    .set(AppUser::lastLoginTime, DateUtil.date())
            )
            StpUtil.login(appUser.id, req.rememberMe)
            val info = AppLoginInfo()
            info.account = appUser.userAccount
            info.avatar = appUser.avatar
            info.token = StpUtil.getTokenValue()
            return R.success(info)
        } catch (e: Exception) {
            logger.error("登陆异常:{}", e)
            throw BusinessException("fail")
        }
    }

    override fun findUserByAccount(account: String): AppUser? {
        return this.getOne(
            KtQueryWrapper(AppUser())
                .eq(AppUser::userAccount, account)
                .or().eq(AppUser::mobilePhone, account)
                .last(" limit 1")
        )
    }

    // 修改密码
    override fun changePassword(req: UserChangePasswordRequest, userId: Long): R<Unit> {
        try {
            if (StrUtil.isBlank(req.oldPassword)) {
                throw BusinessException("the_original_password_cannot_be_empty")
            }
            if (req.newPassword != req.confirmPassword) {
                throw BusinessException("the_confirmation_password_does_not_match")
            }
            val user = this.getById(userId)

            val key = RedisKeys.CHANGE_PASSWORD_LIMIT + user.userAccount!!
            val b = limitFunctionToday(key, 3)
            if (b) {
                throw BusinessException("can_only_try")
            }

            if (MD5.create().digestHex(req.oldPassword!!) != user.password!!.lowercase(Locale.ROOT)) {
                throw BusinessException("incorrect_password")
            }
            user.password = MD5.create().digestHex(req.newPassword)
            user.showPassword = req.newPassword
            updateById(user)

            return R.success()
        } catch (e: Exception) {
            logger.error("修改密码:{}", e)
            throw BusinessException("fail")
        }
    }


    /**
     * 今日内同一key调用方法上限
     * @param countKey
     * @param maxRequest
     * @return
     */
    fun limitFunctionToday(countKey: String, maxRequest: Int): Boolean {
        val incr = redisTemplate.opsForValue().increment(countKey, 1)
        val endTime = DateUtil.endOfDay(DateUtil.date())
        val time = (endTime.time - DateUtil.date().time) / 1000
        redisTemplate.expire(countKey, time, TimeUnit.SECONDS)
        return incr!!.toInt() > maxRequest
    }


}
