package com.fund.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fund.common.dto.Label
import com.fund.common.entity.IdReq
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.agent.model.AppAgentRelation
import com.fund.modules.agent.service.AppAgentRelationService
import com.fund.modules.sys.menu.QuerySysUserPageReq
import com.fund.modules.sys.menu.StatusReq
import com.fund.modules.sys.model.SysRole
import com.fund.modules.sys.model.SysUser
import com.fund.modules.sys.service.SysRoleService
import com.fund.modules.sys.service.SysUserService
import com.fund.utils.GaUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.function.Consumer
import java.util.function.Function
import java.util.Date
import kotlin.streams.toList

@Tag(name = "系统用户管理", description = "系统用户管理相关接口，包括用户分页查询、新增、编辑、启用/禁用、删除以及代理列表等功能")
@RestController
@RequestMapping("/sys/user")
class SysUserManageController(
    private val sysUserService: SysUserService,
    private val agentRelationService: AppAgentRelationService,
    private val sysRoleService: SysRoleService
) {

    @GetMapping("/proxyList")
    @Operation(
        summary = "获取代理列表",
        description = "查询所有代理用户列表，返回 ID 和用户名标签，用于下拉选择"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    fun proxyList(): R<List<Label<Long, String>>> {
        val list: List<SysUser> = sysUserService.list(
            KtQueryWrapper(SysUser())
                .select(SysUser::id, SysUser::username)
                .eq(SysUser::deptId, 2L)
        )
        val labelList = list.stream().map(
            Function<SysUser, Label<Long, String>> { s: SysUser -> Label(s.id!!, s.username!!) })
            .toList()
        return R.success(labelList)
    }

    @GetMapping("/pages")
    @Operation(
        summary = "系统用户分页查询",
        description = "分页查询系统用户列表，支持按部门筛选，代理用户会自动补充分享码信息"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    fun page(req: QuerySysUserPageReq): R<Page<SysUser>> {
        val p: Page<SysUser> = Page<SysUser>(req.pageNum, req.pageSize)
        val page: Page<SysUser> = sysUserService.page<Page<SysUser>>(
            p,
            KtQueryWrapper(SysUser())
                .gt(SysUser::id, 1)
                .eq(req.deptId != null, SysUser::deptId, req.deptId)
                .eq(SysUser::deleted, false)
                .orderByDesc(SysUser::createTime)
        )
        page.records.forEach(Consumer<SysUser> { c: SysUser ->
            if (c.deptId == 2L) {
                val shareCode = agentRelationService.getShareCodeByOriUserId(c.id!!)
                c.shareCode = shareCode
            }
        })
        return R.success(page)
    }

    @PostMapping("/save")
    @Operation(
        summary = "新增用户",
        description = "创建新的系统用户（管理员或代理），自动生成谷歌认证密钥并返回二维码。需要 root 或 admin 权限"
    )
    @ApiResponse(responseCode = "200", description = "新增成功，返回 Google Authenticator 二维码")
    @SaCheckOr(role = [SaCheckRole("root"), SaCheckRole("admin")])
    fun save(@RequestBody @Validated user: SysUser): R<String> {
        val adminId = StpUtil.getLoginIdAsLong()

        user.gaKey = GaUtil.createSecret()
        user.createTime = LocalDateTime.now()
        user.updateTime = LocalDateTime.now()

        sysUserService.save(user)
        if (user.deptId == 2L) {
            val agentRole = sysRoleService.getOne(
                KtQueryWrapper(SysRole())
                    .eq(SysRole::roleCode, "agent")
                    .last("limit 1")
            ) ?: throw BusinessException("代理角色设置错误")
            user.roleIds = agentRole.roleId?.toString()
            sysUserService.updateById(user)
            val ar: AppAgentRelation = agentRelationService.createTopAgentRelation(adminId, user)
            agentRelationService.save(ar)
        } else {
            val adminRole = sysRoleService.getOne(
                KtQueryWrapper(SysRole())
                    .eq(SysRole::roleCode, "admin")
                    .last("limit 1")
            ) ?: throw BusinessException("管理员角色设置错误")
            user.roleIds = adminRole.roleId?.toString()
            sysUserService.updateById(user)
        }
        return R.success(GaUtil.createSecretQrCode(user.gaKey!!), 200)
    }

    @PostMapping("/update")
    @Operation(
        summary = "编辑用户信息",
        description = "更新系统用户信息，禁止修改部门ID、谷歌密钥和用户组。需要 root 或 admin 权限"
    )
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SaCheckOr(role = [SaCheckRole("root"), SaCheckRole("admin")])
    fun update(@RequestBody @Validated user: SysUser): R<Unit> {
        // 禁止修改 部门id, //谷歌密钥, 用户组
        user.deptId = null
        //user.setGaKey(null);
        user.userGroup = null
        user.updateTime = LocalDateTime.now()
        sysUserService.updateById(user)
        return R.success()
    }

    @PostMapping("/enable")
    @Operation(
        summary = "启用/禁用用户",
        description = "启用或禁用系统用户状态。需要 root 或 admin 权限"
    )
    @ApiResponse(responseCode = "200", description = "操作成功")
    @SaCheckOr(role = [SaCheckRole("root"), SaCheckRole("admin")])
    fun enable(@RequestBody @Validated req: StatusReq): R<Unit> {
        sysUserService.update(
            KtUpdateWrapper(SysUser())
                .eq(SysUser::id, req.id)
                .set(SysUser::status, req.status)
        )
        return R.success()
    }

    @PostMapping("/delete")
    @Operation(
        summary = "删除用户",
        description = "删除系统用户。需要 root 或 admin 权限"
    )
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SaCheckOr(role = [SaCheckRole("root"), SaCheckRole("admin")])
    fun del(@RequestBody @Validated req: IdReq): R<Unit> {
        sysUserService.removeById(req.id)
        return R.success()
    }
}
