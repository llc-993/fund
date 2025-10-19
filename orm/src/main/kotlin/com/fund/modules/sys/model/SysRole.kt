package com.fund.modules.sys.model;

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable

/**
 * <p>
 * 后台系统角色
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@TableName("sys_role")
class SysRole : Serializable {

    /**
     * 角色id
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    var roleId: Long? = null

    /**
     * 角色标识
     */
    @TableField("role_code")
    var roleCode: String? = null

    /**
     * 角色名称
     */
    @TableField("role_name")
    var roleName: String? = null

    /**
     * 角色状态  0正常  9停用
     */
    @TableField("role_status")
    var roleStatus: Int? = null

    /**
     * 菜单ids
     */
    @TableField("menu_ids")
    var menuIds: String? = null

    override fun toString(): String {
        return "SysRole{" +
        "roleId=" + roleId +
        ", roleCode=" + roleCode +
        ", roleName=" + roleName +
        ", roleStatus=" + roleStatus +
        ", menuIds=" + menuIds +
        "}"
    }
}
