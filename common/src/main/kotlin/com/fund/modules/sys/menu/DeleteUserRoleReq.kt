package com.fund.modules.sys.menu

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "删除用户角色请求参数")
class DeleteUserRoleReq {
    
    @Schema(description = "角色ID", required = true, example = "1")
    var roleId: Long? = null
    
    @Schema(description = "用户ID集合", required = true, example = "[1, 2, 3]")
    var userIds: List<Long>? = null
}
