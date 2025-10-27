package com.fund.modules.sys.menu

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "删除角色请求参数")
class DeleteRoleReq {
    
    @Schema(description = "角色ID列表", required = true, example = "[1, 2, 3]")
    var roleIds: List<Long>? = null
}
