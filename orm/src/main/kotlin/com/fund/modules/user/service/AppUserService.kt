package com.fund.modules.user.service;

import com.fund.modules.user.model.AppUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fund.common.entity.R
import com.fund.modules.user.UserChangePasswordRequest
import com.fund.modules.user.UserLoginRequest
import com.fund.modules.user.UserRegisterRequest
import com.fund.modules.user.vo.AppLoginInfo
import jakarta.servlet.http.HttpServletRequest

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
interface AppUserService : IService<AppUser> {
    // 注册
    fun register(userRegisterRequest: UserRegisterRequest, request: HttpServletRequest) : R<AppLoginInfo>

    // 登陆
    fun login(req: UserLoginRequest, request: HttpServletRequest): R<Any>

    // 查询账号、手机信息
    fun findUserByAccount(account: String): AppUser?

    // 修改密码
    fun changePassword(req: UserChangePasswordRequest, userId: Long): R<Unit>

}
