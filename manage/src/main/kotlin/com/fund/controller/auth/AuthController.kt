package com.fund.controller.auth

import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.common.Constants
import com.fund.common.entity.R
import com.fund.exception.BusinessException
import com.fund.modules.agent.service.AppAgentRelationService
import com.fund.modules.auth.AdminAuthRequest
import com.fund.modules.auth.AdminLoginUser
import com.fund.modules.sys.dto.AdminInfo
import com.fund.modules.sys.model.SysUser
import com.fund.modules.sys.service.SysRoleService
import com.fund.modules.sys.service.SysUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.Objects
import kotlin.collections.get

@Tag(name = "管理后台登陆", description = "manage-管理后台登陆")
@RestController
class AuthController(
    private val sysUserService: SysUserService,
    private val sysRoleService: SysRoleService,
    private val agentRelationService: AppAgentRelationService
) {

    private val log = KotlinLogging.logger {  }

    @PostMapping(value = ["/login"])
    @SaIgnore
    @Operation(summary = "登陆,和刷单一样")
    fun login(@RequestBody @Validated req: AdminAuthRequest): R<AdminLoginUser> {
        val sysUser = sysUserService.getOne(
            KtQueryWrapper(SysUser())
                .eq(SysUser::username, req.account)
                .eq(SysUser::status, 1)
                .last("limit 1")
        )
        if (Objects.isNull(sysUser)) {
            throw BusinessException("密码或账号有误")
        }
        // 谷歌验证
        if (sysUser.enableSafeMode!! && StrUtil.isNotBlank(sysUser.gaKey)) {
            log.info("谷歌密钥: {}, code: {}", sysUser.gaKey, req.code)
            if (StrUtil.isBlank(req.code)) {
                throw BusinessException("无权限登陆")
            }
            /*if (!GaUtil.auth(sysUser.gaKey, req.code!!)) {
                throw BusinessException("无权限登录")
            }*/
        }

        if (ObjectUtil.isNull(sysUser)) {
            throw BusinessException("密码或账号有误")
        }

        if (req.password != sysUser.password) {
            throw BusinessException("密码或账号有误")
        }

        val isRoot = sysUser.id == 1L

        if (isRoot) {
            val user = AdminLoginUser()

            user.id = sysUser.id
            user.username = sysUser.username
            user.deptId = sysUser.deptId
            user.roleId = 1L
            user.roles = listOf("root")
            user.oriShareCode = agentRelationService.getShareCodeByOriUserId(sysUser.id!!)

            StpUtil.login(user.id, true)
            val info = StpUtil.getTokenInfo()
            user.token = info.tokenValue
            // 设置一些数据
            StpUtil.getSession().set(
                Constants.ADMIN_INFO, AdminInfo(
                    "root",
                    0,
                    user.username ?: "",
                    1L
                )
            )
            return R.success(user)
        }

        val roleIdList = sysUser.getRoleIdList()
        if (CollUtil.isEmpty(roleIdList)) {
            throw BusinessException("用户没有分配角色")
        }
        val roles = sysRoleService.listByIds(sysUser.getRoleIdList())

        val roleCodes = roles.mapNotNull { it.roleCode }

        val roleId = roles[0]?.roleId

        val user = AdminLoginUser()

        user.id = sysUser.id
        user.username = sysUser.username
        user.deptId = sysUser.deptId
        user.roleId = roleId
        user.roles = roleCodes
        user.oriShareCode = agentRelationService.getShareCodeByOriUserId(sysUser.id!!)

        StpUtil.login(user.id, true)
        user.token = StpUtil.getTokenValue()
        // 设置一些数据
        StpUtil.getSession().set(Constants.ADMIN_INFO, AdminInfo(
            "",
            1,
            user.username ?: "",
            -1L
        ))
        return R.success(user)
    }

    @PostMapping(value = ["/logout"])
    @Operation(summary = "退出")
    @SaIgnore
    fun logout(): R<Unit> {
        StpUtil.logout()
        return R.success()
    }

    @PostMapping(value = ["/info"])
    @Operation(summary = "登陆信息")
    fun info(): R<AdminLoginUser> {

        val userId = StpUtil.getLoginIdAsLong()
        val sysUser = sysUserService.getById(userId)
        val isRoot = sysUser.id == 1L

        if (isRoot) {
            val admin = AdminLoginUser()

            admin.id = sysUser.id
            admin.username = sysUser.username
            admin.deptId = sysUser.deptId
            admin.roleId = 1L
            admin.roles = listOf("root")
            admin.oriShareCode = agentRelationService.getShareCodeByOriUserId(sysUser.id!!)

            val info = StpUtil.getTokenInfo()
            admin.token = info.tokenValue
            return R.success(admin)
        }

        val roleIdList = sysUser.getRoleIdList()
        if (CollUtil.isEmpty(roleIdList)) {
            throw BusinessException("用户没有分配角色")
        }
        val roles = sysRoleService.listByIds(sysUser.getRoleIdList())

        val roleCodes = roles.mapNotNull { it.roleCode }

        val roleId = roles[0]?.roleId

        val admin = AdminLoginUser()

        admin.id = sysUser.id
        admin.username = sysUser.username
        admin.deptId = sysUser.deptId
        admin.roleId = roleId
        admin.roles = roleCodes
        admin.oriShareCode = agentRelationService.getShareCodeByOriUserId(sysUser.id!!)
        admin.token = StpUtil.getTokenValue()
        return R.success(admin)
    }
}