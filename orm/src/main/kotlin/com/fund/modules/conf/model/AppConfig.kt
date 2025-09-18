package com.fund.modules.conf.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * app配置
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@TableName("app_config")
class AppConfig : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 标识
     */
    @TableField("code")
    var code: String? = null

    /**
     * 值
     */
    @TableField("value")
    var value: String? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

    /**
     * 创建人
     */
    @TableField("create_by")
    var createBy: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 更新人
     */
    @TableField("update_by")
    var updateBy: String? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppConfig{" +
        "id=" + id +
        ", code=" + code +
        ", value=" + value +
        ", remark=" + remark +
        ", createBy=" + createBy +
        ", createTime=" + createTime +
        ", updateBy=" + updateBy +
        ", updateTime=" + updateTime +
        "}"
    }
}
