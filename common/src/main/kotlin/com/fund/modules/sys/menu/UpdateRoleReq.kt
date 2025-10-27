package com.fund.modules.sys.menu

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "更新角色请求参数")
class UpdateRoleReq {
    
    @Schema(description = "角色ID", required = true, example = "1")
    var roleId: Long? = null

    @Schema(description = "角色名称", required = true, example = "管理员")
    var roleName: String? = null

    @Schema(
        description = "角色状态",
        example = "0",
        allowableValues = ["0", "9"]
    )
    var roleStatus: Int? = null
}
