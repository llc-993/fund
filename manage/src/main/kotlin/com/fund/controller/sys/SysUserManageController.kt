package org.lemon.api.controller.sys

import cn.dev33.satoken.annotation.SaCheckOr
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.date.DateUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.lemon.api.common.domain.R
import org.lemon.api.common.domain.co.IdReq

import org.lemon.api.common.domain.dto.Label
import org.lemon.api.common.exception.BusinessException
import org.lemon.api.common.utils.GaUtil
import org.lemon.api.modules.agent.domain.entity.AppAgentRelation
import org.lemon.api.modules.agent.service.AppAgentRelationService
import org.lemon.api.modules.sys.domain.co.QuerySysUserPageReq
import org.lemon.api.modules.sys.domain.co.StatusReq
import org.lemon.api.modules.sys.domain.entity.SysRole
import org.lemon.api.modules.sys.domain.entity.SysUser
import org.lemon.api.modules.sys.service.SysRoleService
import org.lemon.api.modules.sys.service.SysUserService
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.function.Consumer
import java.util.function.Function
import kotlin.streams.toList

@Api(tags = ["manage-系统用户管理"])
@RestController
@RequestMapping("/manage/sys/user")
class SysUserManageController(
    private val sysUserService: SysUserService,
    private val agentRelationService: AppAgentRelationService,
    private val sysRoleService: SysRoleService
) {

    @GetMapping("/proxyList")
    @ApiOperation("代理列表下拉")
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
    @ApiOperation("系统用户分页")
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
    @ApiOperation("新增用户/代理")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun save(@RequestBody @Validated user: SysUser): R<String> {
        val adminId = StpUtil.getLoginIdAsLong()

        user.gaKey = GaUtil.createSecret()
        user.createTime = DateUtil.date()
        user.updateTime = DateUtil.date()

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
    @ApiOperation("编辑用户")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun update(@RequestBody @Validated user: SysUser): R<Unit> {
        // 禁止修改 部门id, //谷歌密钥, 用户组
        user.deptId = null
        //user.setGaKey(null);
        user.userGroup = null
        user.updateTime = DateUtil.date()
        sysUserService.updateById(user)
        return R.success()
    }

    @PostMapping("/enable")
    @ApiOperation("启用禁用")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun enable(@RequestBody @Validated req: StatusReq): R<Unit> {
        sysUserService.update(
            KtUpdateWrapper(SysUser())
                .eq(SysUser::id, req.id)
                .set(SysUser::status, req.status)
        )
        return R.success()
    }

    @PostMapping("/delete")
    @ApiOperation("删除用户")
    @SaCheckOr(
        role = [SaCheckRole("root"), SaCheckRole("admin")]
    )
    fun del(@RequestBody @Validated req: IdReq): R<Unit> {
        sysUserService.removeById(req.id)
        return R.success()
    }
}
