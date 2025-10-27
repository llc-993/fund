package com.fund.modules.sys.menu

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "新增菜单请求参数")
class AddMenuReq {
    
    @Schema(description = "菜单名称", required = true, example = "系统管理")
    var menuName: String? = null

    @Schema(description = "父菜单ID，0表示顶级菜单", example = "0", nullable = true)
    var parentId: Long? = null

    @Schema(description = "显示顺序，数字越小越靠前", example = "1", nullable = true)
    var orderNum: Int? = null

    @Schema(description = "路由地址", example = "/system", nullable = true)
    var path: String? = null

    @Schema(description = "组件路径", example = "layout/index", nullable = true)
    var component: String? = null

    @Schema(description = "路由参数", example = "id=1", nullable = true)
    var query: String? = null

    @Schema(
        description = "菜单类型",
        required = true,
        example = "1",
        allowableValues = ["1", "2", "3"]
    )
    var menuType: Int? = null

    @Schema(description = "菜单状态（true=正常，false=停用）", example = "true", nullable = true)
    var status: Boolean? = null

    @Schema(description = "权限标识", example = "system:menu:list", nullable = true)
    var perms: String? = null

    @Schema(description = "菜单图标", example = "system", nullable = true)
    var icon: String? = null
}
