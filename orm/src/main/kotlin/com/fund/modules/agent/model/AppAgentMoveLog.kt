package com.fund.modules.agent.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 代理迁移记录
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@TableName("app_agent_move_log")
class AppAgentMoveLog : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 迁移者
     */
    @TableField("from_user_account")
    var fromUserAccount: String? = null

    /**
     * 接收者
     */
    @TableField("to_user_account")
    var toUserAccount: String? = null

    /**
     * 迁移内容
     */
    @TableField("content")
    var content: String? = null

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

    override fun toString(): String {
        return "AppAgentMoveLog{" +
        "id=" + id +
        ", fromUserAccount=" + fromUserAccount +
        ", toUserAccount=" + toUserAccount +
        ", content=" + content +
        ", createBy=" + createBy +
        ", createTime=" + createTime +
        "}"
    }
}
