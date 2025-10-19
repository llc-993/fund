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
 * 客服链接列表
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@TableName("sys_cs_link")
class SysCsLink : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 客服名称
     */
    @TableField("name")
    var name: String? = null

    /**
     * 启用
     */
    @TableField("enable")
    var enable: Boolean? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 链接地址
     */
    @TableField("link")
    var link: String? = null

    /**
     * 操作人
     */
    @TableField("update_by")
    var updateBy: String? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "SysCsLink(id=$id, name=$name, enable=$enable, createTime=$createTime, link=$link, updateBy=$updateBy, updateTime=$updateTime)"
    }
}
