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
 * 操作日志
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@TableName("sys_opt_log")
class SysOptLog : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 操作人
     */
    @TableField("opt_user")
    var optUser: String? = null

    /**
     * ip地址
     */
    @TableField("ip")
    var ip: String? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

    /**
     * 参数（JSON格式）
     */
    @TableField("json")
    var json: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    override fun toString(): String {
        return "SysOptLog{" +
        "id=" + id +
        ", optUser=" + optUser +
        ", ip=" + ip +
        ", remark=" + remark +
        ", json=" + json +
        ", createTime=" + createTime +
        "}"
    }
}
