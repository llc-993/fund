package com.fund.modules.sys.menu

import io.swagger.v3.oas.annotations.media.Schema
import org.jetbrains.annotations.NotNull

@Schema(description = "设置角色菜单请求参数")
class SetRoleMenuReq {
    
    @Schema(description = "角色ID", required = true, example = "1")
    @NotNull
    var roleId: Long? = null

    @Schema(description = "菜单ID集合", required = true, example = "[1, 2, 3, 4]")
    @NotNull
    var menuIds: List<Long>? = null
}
