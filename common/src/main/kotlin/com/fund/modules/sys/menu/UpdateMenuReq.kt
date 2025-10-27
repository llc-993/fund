package com.fund.modules.sys.menu


import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.format.annotation.DateTimeFormat
import java.util.*


@Schema(description = "更新菜单请求参数")
class UpdateMenuReq {
    
    @Schema(description = "菜单ID", required = true, example = "1")
    var menuId: Long? = null

    @Schema(description = "菜单名称", example = "系统管理")
    var menuName: String? = null

    @Schema(description = "父菜单ID，0表示顶级菜单", example = "0")
    var parentId: Long? = null

    @Schema(description = "显示顺序，数字越小越靠前", example = "1")
    var orderNum: Int? = null

    @Schema(description = "路由地址", example = "/system")
    var path: String? = null

    @Schema(description = "组件路径", example = "layout/index")
    var component: String? = null

    @Schema(description = "路由参数", example = "id=1")
    var query: String? = null

    @Schema(
        description = "菜单类型",
        example = "1",
        allowableValues = ["1", "2", "3"]
    )
    var menuType: Int? = null

    @Schema(description = "菜单状态（true=启用，false=停用）", example = "true")
    var status: Boolean? = null

    @Schema(description = "权限标识", example = "system:menu:list")
    var perms: String? = null

    @Schema(description = "菜单图标", example = "system")
    var icon: String? = null

    @Schema(description = "创建者")
    var createBy: String? = null

    @Schema(description = "更新人")
    var updateBy: String? = null

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createTime: Date? = null

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var updateTime: Date? = null
}
