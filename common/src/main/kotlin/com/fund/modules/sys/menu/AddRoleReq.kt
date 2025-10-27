package com.fund.modules.sys.menu

import io.swagger.v3.oas.annotations.media.Schema
import org.jetbrains.annotations.NotNull

@Schema(description = "新增角色请求参数")
class AddRoleReq {
    
    @Schema(description = "角色名称", required = true, example = "管理员")
    @NotNull
    var roleName: String? = null

    @Schema(description = "角色标识/代码", required = true, example = "admin")
    @NotNull
    var roleCode: String? = null

    @Schema(
        description = "角色状态",
        required = true,
        example = "0",
        allowableValues = ["0", "9"]
    )
    @NotNull
    var roleStatus: Int? = null
}
