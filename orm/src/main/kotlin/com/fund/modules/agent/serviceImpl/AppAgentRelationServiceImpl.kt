package com.fund.modules.agent.serviceImpl;

import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.RandomUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.modules.agent.model.AppAgentRelation;
import com.fund.modules.agent.mapper.AppAgentRelationMapper;
import com.fund.modules.agent.service.AppAgentRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.modules.sys.model.SysUser
import com.fund.modules.sys.service.SysUserService
import com.fund.modules.user.model.AppUser
import com.fund.utils.RedisLockService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service;
import java.time.LocalDateTime
import kotlin.compareTo
import kotlin.text.contains

/**
 * <p>
 * 代理层级关联表 服务实现类
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@Service
open class AppAgentRelationServiceImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val sysUserService: SysUserService,
) : ServiceImpl<AppAgentRelationMapper, AppAgentRelation>(), AppAgentRelationService {

    override fun findAgentByCode(oriCode: String): AppAgentRelation? {
        return getOne(
            KtQueryWrapper(AppAgentRelation())
                .eq(AppAgentRelation::oriShareCode, oriCode)
                .last("limit 1")
        )
    }

    override fun createMemAgentRelation(user: AppUser, ar: AppAgentRelation): AppAgentRelation {
        val myAg = AppAgentRelation()
        myAg.oriAccount = user.userAccount
        myAg.oriUserId = user.id

        //val myShareCode = GeneratorIdUtil.generateForId(user.id)
        myAg.oriShareCode = genShareCode()

        // 顶级代理

        // 顶级代理
        myAg.topUserId = ar.topUserId
        myAg.topShareCode = ar.topShareCode

        // 级别 : (0)-总代 (1)-一级代理 (2)-二级代理 (3-无限)-会员
        myAg.level = ar.level?.plus(1)
        // 继承上级的用户组

        myAg.userGroup = ar.userGroup

        // 1级代理（直属上级）
        myAg.p1Id = ar.oriUserId
        myAg.p1Account = ar.oriAccount
        myAg.p1Code = ar.oriShareCode

        myAg.p2Id = ar.p1Id
        myAg.p2Code = ar.p1Code
        myAg.p2Account = ar.p1Account

        myAg.p3Id = ar.p2Id
        myAg.p3Code = ar.p2Code
        myAg.p3Account = ar.p2Account

        myAg.createBy = user.userAccount
        myAg.createTime = LocalDateTime.now()
        myAg.updateTime = LocalDateTime.now()

        return myAg
    }

    private fun genShareCode(): String {
        return RedisLockService.lock("genShareCode") {
            val shareCodeList = list(
                KtQueryWrapper(
                    AppAgentRelation()
                ).select(AppAgentRelation::oriShareCode)
            ).map { it.oriShareCode }.toList()

            val count = shareCodeList.size
            val length = if (count >= 50000) 8 else 6
            var code: String
            var exits: Boolean
            do {
                code = RandomUtil.randomString("FLGW5XC39ZM67YRT2HS8DVEJ4KQPUANB", length)
                exits = shareCodeList.contains(code)
            } while (exits)
            code
        }
    }

    override fun getTopIdByUserIdFromCache(userId: Long): Long {
        val value = redisTemplate.opsForHash<String, Number>().get(RedisKeys.TOP_AGENT_MAP_CACHE_KEY, userId.toString())
        if (value == null) {
            val topId:Long = baseMapper.getTopIdByOriUserId(userId) ?: -1L

            redisTemplate.opsForHash<String, Long>().put(RedisKeys.TOP_AGENT_MAP_CACHE_KEY, userId.toString(), topId)
            return topId
        }

        return value.toLong()
    }

    override fun getShareCodeByOriUserId(userId: Long): String? {
        val ar = getOne(
            KtQueryWrapper(AppAgentRelation())
                .select(AppAgentRelation::oriShareCode)
                .eq(AppAgentRelation::oriUserId, userId)
                .last("limit 1")
        )
        return ar?.oriShareCode
    }

    override fun createTopAgentRelation(
        adminId: Long,
        sysUser: SysUser
    ): AppAgentRelation {
        val admin = sysUserService.getById(adminId)
        val userId = sysUser.id
        val account = sysUser.username
        // 生成邀请码
        val ag = AppAgentRelation()
        val code = genShareCode() // GeneratorIdUtil.generateForId(userId)
        ag.topUserId = userId
        ag.topShareCode = code

        ag.oriUserId = userId
        ag.oriShareCode = code
        ag.oriAccount = account

        // 级别 : (0)-总代 (1)-一级代理 (2)-二级代理 (3-无限)-会员
        ag.level = 0
        // 总代的用户组默认是正式组
        ag.userGroup = sysUser.userGroup

        ag.createBy = admin.username
        ag.createTime = LocalDateTime.now()
        ag.updateBy = admin.username
        ag.updateTime = LocalDateTime.now()
        return ag
    }
}
