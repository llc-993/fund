package com.fund.modules.sys.vo

import io.swagger.v3.oas.annotations.media.Schema
import java.util.ArrayList

@Schema(description = "用户权限/菜单信息，树状结构")
class UserPermissionsVO {
    
    @Schema(description = "菜单ID", nullable = true)
    var menuId: Long? = null

    @Schema(description = "菜单名称", example = "系统管理", nullable = true)
    var menuName: String? = null

    @Schema(description = "名称", example = "system", nullable = true)
    var name: String? = null

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
        example = "1",
        allowableValues = ["1", "2", "3"],
        nullable = true
    )
    var menuType: Int? = null

    @Schema(
        description = "菜单状态",
        example = "true",
        nullable = true
    )
    var status: Boolean? = null

    @Schema(description = "权限标识", example = "system:user:list", nullable = true)
    var perms: String? = null

    @Schema(description = "菜单图标", example = "system", nullable = true)
    var icon: String? = null

    @Schema(description = "路由meta信息，用于前端路由配置")
    var meta: Meta = Meta()

    @Schema(description = "子菜单列表，树状结构")
    var children: List<UserPermissionsVO> = mutableListOf()


    @Schema(description = "路由meta元信息")
    class Meta {
        @Schema(description = "菜单标题", example = "系统管理")
        var title: String? = null

        @Schema(description = "菜单图标", example = "system")
        var icon: String? = null
    }
}
