package com.fund.modules.agent.serviceImpl;

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.modules.agent.model.AppAgentRelation;
import com.fund.modules.agent.mapper.AppAgentRelationMapper;
import com.fund.modules.agent.service.AppAgentRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fund.common.RedisKeys
import com.fund.exception.BusinessException
import com.fund.modules.agent.model.AppAgentMoveLog
import com.fund.modules.agent.model.dto.AgentFloor
import com.fund.modules.agent.model.dto.AgentLineQuery
import com.fund.modules.agent.model.dto.AgentMoveCo
import com.fund.modules.agent.model.dto.AgentTreePageQuery
import com.fund.modules.agent.model.dto.AgentUserBase
import com.fund.modules.agent.service.AppAgentMoveLogService
import com.fund.modules.sys.model.SysUser
import com.fund.modules.sys.service.SysUserService
import com.fund.modules.user.model.AppUser
import com.fund.utils.DTOUtil
import com.fund.utils.RedisLockService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service;
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.Comparator
import java.util.LinkedList
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.stream.Collector
import java.util.stream.Collectors
import kotlin.collections.addAll
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
    private val agentMoveLogService: AppAgentMoveLogService,
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
            val topId: Long = baseMapper.getTopIdByOriUserId(userId) ?: -1L

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

    override fun queryAgentPage(
        topId: Long?,
        query: AgentTreePageQuery
    ): Page<AgentUserBase> {
        val p: Page<AgentUserBase> = Page(query.pageNum, query.pageSize)
        if (StrUtil.isBlank(query.proxyUserName) && StrUtil.isBlank(query.proxyShareCode)) {
            if (topId == null) {
                query.proxyId = -1L
            } else {
                query.proxyId = topId
            }
        }
        return baseMapper.queryAgentPage(topId, p, query)
    }

    override fun queryAgentLinePage(
        topId: Long?,
        query: AgentLineQuery
    ): Page<AgentUserBase> {
        val p: Page<AgentUserBase> = Page(query.pageNum, query.pageSize)
        return baseMapper.queryAgentLinePage(topId, p, query)
    }

    override fun agentMove(userId: Long, co: AgentMoveCo): Boolean {
        return RedisLockService.transaction block@{
            if (co.fromUserAccount == co.toUserAccount) {
                throw BusinessException("迁移者不能和接收者相同")
            }
            val admin = sysUserService.getById(userId)
            // 检查双方代理关系情况，是否存在

            val from = baseMapper.selectOne(
                KtQueryWrapper(AppAgentRelation())
                    .eq(AppAgentRelation::oriAccount, co.fromUserAccount)
                    .last("limit 1")
            ) ?: throw BusinessException("找不到迁移者")
            if (from.p1Id == -1L) {
                throw BusinessException("不支持总代迁移")
            }
            // 迁移的目标刚好是它的上级，不需要迁移。

            if (from.p1Account == co.toUserAccount) {
                throw BusinessException("无需迁移")
            }
            val to = baseMapper.selectOne(
                KtQueryWrapper(AppAgentRelation())
                    .eq(AppAgentRelation::oriAccount, co.toUserAccount)
                    .last("limit 1")
            ) ?: throw BusinessException("找不到接收者")
            // 代理关系缓存清理。

            redisTemplate.delete(RedisKeys.TOP_AGENT_MAP_CACHE_KEY)
            // 这是当前的整个代理线关系map

            val agentList = baseMapper.selectList(
                KtQueryWrapper(AppAgentRelation())
                    .eq(AppAgentRelation::topUserId, from.topUserId)
            )
            // 找出所有下级

            val agentFloors: LinkedList<AgentFloor> = AgentFloor.findAgentFloors(from.oriUserId!!, agentList)
            from.p1Id = to.oriUserId
            from.p1Account = to.p1Account
            from.p1Code = to.oriShareCode
            agentFloors.addFirst(AgentFloor(0, from))

            // 不能往下级迁移

            // 不能往下级迁移
            val isToFloor = agentFloors.stream().map(Function<AgentFloor, Long> { f: AgentFloor ->
                f.agent.oriUserId!!
            }).anyMatch { id: Long -> id == to.oriUserId }

            if (isToFloor) {
                throw BusinessException("不支持向下级迁移")
            }

            // 此系统不允许跨线
            if (from.topUserId != to.topUserId) {
                throw BusinessException("不支持的操作")
            }
            if (from.topUserId != 1L) {
                throw BusinessException("不支持的操作")
            }
            if (to.topUserId != 1L) {
                throw BusinessException("不支持的操作")
            }

            // 跨线
            if (from.topUserId!! != to.topUserId) {
                agentList.addAll(
                    baseMapper.selectList(
                        KtQueryWrapper(AppAgentRelation())
                            .eq(AppAgentRelation::topUserId, to.topUserId)
                    )
                )
            }

            val toMap:Collector<AppAgentRelation, *, Map<Long, AppAgentRelation>> = Collectors.toMap<AppAgentRelation, Long, AppAgentRelation>(
                AppAgentRelation::oriUserId,
                Function.identity(),
                BinaryOperator { o: AppAgentRelation, n: AppAgentRelation -> n }
            )

            val agents: Map<Long, AppAgentRelation> = agentList.stream().collect(toMap)

            val sortedAgentFloorList: List<AgentFloor> =
                agentFloors.stream().sorted(Comparator.comparing<AgentFloor, Int>(AgentFloor::floor))
                    .collect(Collectors.toList<AgentFloor>())

            val updateList: MutableList<AppAgentRelation?> = ArrayList()
            // 迁移过程
            // 迁移过程
            for (f in sortedAgentFloorList) {
                val la = agents[f.agent.oriUserId]
                val lb = agents[f.agent.p1Id]
                la!!.level = f.floor // 代理层级
                la.topUserId = lb!!.topUserId
                la.topShareCode = lb.topShareCode
                la.p1Id = lb.oriUserId
                la.p1Account = lb.oriAccount
                la.p1Code = lb.oriShareCode
                la.p2Id = lb.p1Id
                la.p2Code = lb.p1Code
                la.p2Account = lb.p1Account
                la.p3Id = lb.p2Id
                la.p3Code = lb.p2Code
                la.p3Account = lb.p2Account
                updateList.add(la)
            }

            if (CollUtil.isNotEmpty(updateList)) {
                val moveLog = AppAgentMoveLog()
                moveLog.fromUserAccount = co.fromUserAccount
                moveLog.toUserAccount = co.toUserAccount
                moveLog.createBy = admin.username
                moveLog.createTime = LocalDateTime.now()
                moveLog.content =
                    StrUtil.format(
                        "迁移者账号:[{}] -> 接收者账号:[{}].共迁移{}条记录",
                        co.fromUserAccount,
                        co.toUserAccount,
                        updateList.size
                    )

                agentMoveLogService.save(moveLog)
                for (list in DTOUtil.partition(updateList, 1000)) {
                    updateBatchById(list)
                }
                return@block true
            }
            return@block false
        }
    }

    override fun topAgentMove(userId: Long, co: AgentMoveCo): Boolean {
        return RedisLockService.transaction block@{
            if (co.fromUserAccount == co.toUserAccount) {
                throw BusinessException("迁移者不能和接收者相同")
            }
            val admin = sysUserService.getById(userId)
            // 检查双方代理关系情况，是否存在
            val from = baseMapper.selectOne(
                KtQueryWrapper(AppAgentRelation())
                    .eq(AppAgentRelation::oriAccount, co.fromUserAccount)
                    .last("limit 1")
            )
                ?: throw BusinessException("找不到迁移者")
            if (from.topUserId != from.oriUserId) {
                throw BusinessException("迁移者不是顶级代理")
            }
            val to = baseMapper.selectOne(
                KtQueryWrapper(AppAgentRelation())
                    .eq(AppAgentRelation::oriAccount, co.toUserAccount)
                    .last("limit 1")
            )
                ?: throw BusinessException("找不到接收者")
            if (to.topUserId != to.oriUserId) {
                throw BusinessException("接收者不是顶级代理")
            }
            // 代理关系缓存清理。
            redisTemplate.delete(RedisKeys.TOP_AGENT_MAP_CACHE_KEY)

            // 这是当前的整个代理线关系map

            val agentList = baseMapper.selectList(
                KtQueryWrapper(AppAgentRelation())
                    .eq(AppAgentRelation::topUserId, from.topUserId)
            )

            // 找出所有下级

            val agentFloors: List<AgentFloor> = AgentFloor.findAgentFloors(from.oriUserId!!, agentList)

            agentList.addAll(
                baseMapper.selectList(
                    KtQueryWrapper(AppAgentRelation())
                        .eq(AppAgentRelation::topUserId, to.topUserId)
                )
            )
            val agents: Map<Long, AppAgentRelation> = agentList.stream().collect(
                Collectors.toMap(AppAgentRelation::oriUserId,
                    Function.identity(),
                    BinaryOperator { o: AppAgentRelation, n: AppAgentRelation -> n })
            )
            val sortedAgentFloorList: List<AgentFloor> =
                agentFloors.stream().sorted(Comparator.comparing<AgentFloor, Int>(AgentFloor::floor))
                    .collect(Collectors.toList<AgentFloor>())

            val updateList: MutableList<AppAgentRelation?> = ArrayList()
            // 所有的一级下级

            for (one in agentFloors.stream().filter { f: AgentFloor -> f.floor == 1 }
                .collect(Collectors.toList<AgentFloor>())) {
                val agent = agents[one.agent.oriUserId]
                agent!!.topUserId = to.topUserId
                agent.topShareCode = to.topShareCode
                agent.p1Id = to.oriUserId
                agent.p1Account = to.oriAccount
                agent.p1Code = to.oriShareCode
            }
            // 迁移过程
            for (f in sortedAgentFloorList) {
                val la = agents[f.agent.oriUserId]
                val lb = agents[f.agent.p1Id]
                la!!.level = f.floor // 代理层级
                la.topUserId = lb!!.topUserId
                la.topShareCode = lb.topShareCode
                la.p1Id = lb.oriUserId
                la.p1Account = lb.oriAccount
                la.p1Code = lb.oriShareCode
                la.p2Id = lb.p1Id
                la.p2Code = lb.p1Code
                la.p2Account = lb.p1Account
                la.p3Id = lb.p2Id
                la.p3Code = lb.p2Code
                la.p3Account = lb.p2Account
                updateList.add(la)
            }

            if (CollUtil.isNotEmpty(updateList)) {
                val moveLog = AppAgentMoveLog()
                moveLog.fromUserAccount = co.fromUserAccount
                moveLog.toUserAccount = co.toUserAccount
                moveLog.createBy = admin.username
                moveLog.createTime = LocalDateTime.now()
                moveLog.content =
                    StrUtil.format(
                        "迁移者账号:[{}] -> 接收者账号:[{}].共迁移{}条记录",
                        co.fromUserAccount,
                        co.toUserAccount,
                        updateList.size
                    )

                agentMoveLogService.save(moveLog)
                for (list in DTOUtil.partition(updateList, 1000)) {
                    updateBatchById(list)
                }
                return@block true
            }
            return@block false
        }
    }
}
