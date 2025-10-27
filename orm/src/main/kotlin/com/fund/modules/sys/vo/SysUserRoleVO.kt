package com.fund.modules.sys.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "系统用户角色信息")
class SysUserRoleVO {
    
    @Schema(description = "用户ID", nullable = true)
    var id: Long? = null

    @Schema(description = "用户名/登录账号", example = "admin", nullable = true)
    var username: String? = null

    @Schema(description = "用户昵称/显示名称", example = "张三", nullable = true)
    var nickname: String? = null
}
