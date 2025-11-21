package com.fund.modules.agent.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.modules.agent.model.AppAgentRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.modules.agent.model.dto.AgentLineQuery
import com.fund.modules.agent.model.dto.AgentMoveCo
import com.fund.modules.agent.model.dto.AgentTreePageQuery
import com.fund.modules.agent.model.dto.AgentUserBase
import com.fund.modules.sys.model.SysUser
import com.fund.modules.user.model.AppUser

/**
 * <p>
 * 代理层级关联表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
interface AppAgentRelationService : IService<AppAgentRelation> {
    /**
     * 根据邀请码查询对象
     */
    fun findAgentByCode(oriCode: String): AppAgentRelation?

    // 创建代理关系
    fun createMemAgentRelation(user: AppUser, ar: AppAgentRelation): AppAgentRelation

    /**
     * 从缓存中获取用户的顶级代理id
     * @param userId
     * @return
     */
    fun getTopIdByUserIdFromCache(userId: Long): Long

    fun getShareCodeByOriUserId(userId: Long): String?

    fun createTopAgentRelation(adminId:Long ,sysUser: SysUser): AppAgentRelation

    fun queryAgentPage(topId: Long?, query: AgentTreePageQuery): Page<AgentUserBase>

    fun queryAgentLinePage(topId: Long?, query: AgentLineQuery): Page<AgentUserBase>

    fun agentMove(userId: Long, co: AgentMoveCo): Boolean

    fun topAgentMove(userId: Long, co: AgentMoveCo): Boolean
}
