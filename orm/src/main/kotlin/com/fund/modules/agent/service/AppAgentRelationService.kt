package com.fund.modules.agent.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.modules.agent.model.AppAgentRelation;
import com.baomidou.mybatisplus.extension.service.IService;
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

}
