package com.fund.modules.sys.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@TableName("sys_menu")
class SysMenu : Serializable {

    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.AUTO)
    var menuId: Long? = null

    /**
     * 菜单名称
     */
    @TableField("menu_name")
    var menuName: String? = null

    /**
     * 父菜单ID
     */
    @TableField("parent_id")
    var parentId: Long? = null

    /**
     * 显示顺序
     */
    @TableField("order_num")
    var orderNum: Int? = null

    /**
     * 路由地址
     */
    @TableField("path")
    var path: String? = null

    /**
     * 组件路径
     */
    @TableField("component")
    var component: String? = null

    /**
     * 路由参数
     */
    @TableField("query")
    var query: String? = null

    /**
     * 菜单类型（1-目录 2-菜单 3-按钮）
     */
    @TableField("menu_type")
    var menuType: Int? = null

    /**
     * 菜单状态（0正常 1停用）
     */
    @TableField("status")
    var status: Boolean? = null

    /**
     * 权限标识
     */
    @TableField("perms")
    var perms: String? = null

    /**
     * 菜单图标
     */
    @TableField("icon")
    var icon: String? = null

    /**
     * 创建者
     */
    @TableField("create_by")
    var createBy: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 更新者
     */
    @TableField("update_by")
    var updateBy: String? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

    override fun toString(): String {
        return "SysMenu{" +
        "menuId=" + menuId +
        ", menuName=" + menuName +
        ", parentId=" + parentId +
        ", orderNum=" + orderNum +
        ", path=" + path +
        ", component=" + component +
        ", query=" + query +
        ", menuType=" + menuType +
        ", status=" + status +
        ", perms=" + perms +
        ", icon=" + icon +
        ", createBy=" + createBy +
        ", createTime=" + createTime +
        ", updateBy=" + updateBy +
        ", updateTime=" + updateTime +
        ", remark=" + remark +
        "}"
    }
}
