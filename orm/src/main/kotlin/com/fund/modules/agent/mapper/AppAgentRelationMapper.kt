package com.fund.modules.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.modules.agent.model.AppAgentRelation;
import com.fund.modules.agent.model.dto.AgentLineQuery
import com.fund.modules.agent.model.dto.AgentTreePageQuery
import com.fund.modules.agent.model.dto.AgentUserBase
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param

/**
 * <p>
 * 代理层级关联表 Mapper 接口
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@Mapper
interface AppAgentRelationMapper : BaseMapper<AppAgentRelation> {


    fun getTopIdByOriUserId(@Param("userId") userId: Long?): Long?

    // 代理下级分页
    fun queryAgentLinePage(
        @Param("topUserId") topUserId: Long?,
        @Param("page") p: Page<AgentUserBase>,
        @Param("query") query: AgentLineQuery
    ): Page<AgentUserBase>

    fun queryAgentPage(
        @Param("topUserId") topUserId: Long?,
        @Param("page") page: Page<AgentUserBase>,
        @Param("req") req: AgentTreePageQuery
    ): Page<AgentUserBase>

    fun countLine1ByUserId(@Param("userId") userId: Long): Int

    fun countLine2ByUserId(@Param("userId") userId: Long): Int

    fun countLine3ByUserId(@Param("userId") userId: Long): Int

}
