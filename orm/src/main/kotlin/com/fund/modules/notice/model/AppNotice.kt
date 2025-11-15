package com.fund.modules.notice.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 系统公告
 * </p>
 *
 * @author 书记
 * @since 2025-11-14
 */
@Schema(description = "系统公告信息")
@TableName("app_notice")
class AppNotice : Serializable {

    /**
     * 公告ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "公告ID", example = "8", nullable = true)
    var id: Long? = null

    /**
     * 公告标题
     */
    @TableField("title")
    @Schema(description = "公告标题", example = "维护通知", nullable = true)
    var title: String? = null

    /**
     * 公告类型（例如：1=公告，2=活动）
     */
    @TableField("type")
    @Schema(description = "公告类型（1=公告，2=活动等）", example = "2", nullable = true)
    var type: Byte? = null

    /**
     * 公告内容
     */
    @TableField("content")
    @Schema(description = "公告内容，支持富文本", example = "用ACR做投資交易…", nullable = true)
    var content: String? = null

    /**
     * 0=关闭，1=启用
     */
    @TableField("status")
    @Schema(description = "公告状态（0=关闭，1=启用）", example = "1", nullable = true)
    var status: Byte? = null

    /**
     * 语言编码，如 zh-cn、zh-tw
     */
    @TableField("language")
    @Schema(description = "语言编码", example = "zh-tw", nullable = true)
    var language: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2025-03-22T10:39:04", nullable = true)
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2025-03-22T11:00:00", nullable = true)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppNotice{" +
        "id=" + id +
        ", title=" + title +
        ", type=" + type +
        ", content=" + content +
        ", status=" + status +
        ", language=" + language +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}"
    }
}
