package com.fund.controller.agent

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.entity.PageReq
import com.fund.common.entity.R
import com.fund.modules.agent.model.AppAgentMoveLog
import com.fund.modules.agent.model.dto.AgentLineQuery
import com.fund.modules.agent.model.dto.AgentMoveCo
import com.fund.modules.agent.model.dto.AgentTreePageQuery
import com.fund.modules.agent.model.dto.AgentUserBase
import com.fund.modules.agent.service.AppAgentMoveLogService
import com.fund.modules.agent.service.AppAgentRelationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "代理管理", description = "manage-管理后台代理管理")
@RestController
@RequestMapping("/agent")
class AgentController(
    private val agentRelationService: AppAgentRelationService,
    private val moveLogService: AppAgentMoveLogService
) {

    @GetMapping("/agentMoveLogPage")
    @Operation(summary = "代理线迁移记录")
    fun agentMoveLogPage(req: PageReq): R<Page<AppAgentMoveLog>> {
        val p: Page<AppAgentMoveLog> = Page<AppAgentMoveLog>(req.pageNum, req.pageSize)
        return R.success(moveLogService.page(p))
    }

    @Operation(summary =  "查询代理-分页")
    @GetMapping("/queryAgentPage")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin"), SaCheckRole("agent")]
    )
    fun queryAgentPage(
        @RequestParam(required = false) @Parameter(description ="顶级代理id") topId: Long?,
        query: AgentTreePageQuery
    ): R<Page<AgentUserBase>> {
        val userId = StpUtil.getLoginIdAsLong()
        val isRoot = userId == 1L

        if (!isRoot){
            return R.success(agentRelationService.queryAgentPage(userId, query))
        }
        return R.success(agentRelationService.queryAgentPage(topId, query))
    }


    @Operation(summary = "查询代理直属下级分页")
    @GetMapping("/agentLine")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin"), SaCheckRole("agent")]
    )
    fun agentLine(
        @RequestParam(required = false) topId: Long?,
        query: AgentLineQuery
    ): R<Page<AgentUserBase>> {

        val userId = StpUtil.getLoginIdAsLong()
        val isRoot = userId == 1L

        if (!isRoot){
            return R.success(agentRelationService.queryAgentLinePage(userId, query))
        }
        return R.success(agentRelationService.queryAgentLinePage(topId, query))
    }

    @Operation(summary = "代理线迁移-下级迁移")
    @PostMapping("/agentMove")
    @SaCheckRole("root")
    fun agentMove(@Validated @RequestBody co: AgentMoveCo): R<Unit> {
        val userId = StpUtil.getLoginIdAsLong()
        agentRelationService.agentMove(userId, co)
        return R.success()
    }

    @Operation(summary = "代理线迁移-总代迁移")
    @PostMapping("/topAgentMove")
    @SaCheckRole("root")
    fun topAgentMove(@Validated @RequestBody co: AgentMoveCo): R<Unit> {
        val userId = StpUtil.getLoginIdAsLong()
        agentRelationService.topAgentMove(userId, co)
        return R.success()
    }

}