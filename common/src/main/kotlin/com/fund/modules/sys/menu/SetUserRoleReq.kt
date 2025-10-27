package com.fund.modules.sys.menu

import io.swagger.v3.oas.annotations.media.Schema
import org.jetbrains.annotations.NotNull

@Schema(description = "设置用户角色请求参数")
class SetUserRoleReq {
    
    @Schema(description = "角色ID", required = true, example = "1")
    @NotNull
    var roleId: Long? = null

    @Schema(description = "用户ID集合", required = true, example = "[1, 2, 3]")
    @NotNull
    var userIds: List<Long>? = null
}
