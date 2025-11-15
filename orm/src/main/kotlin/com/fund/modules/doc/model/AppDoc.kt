package com.fund.modules.doc.model;

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
 * APP文案
 * </p>
 *
 * @author 书记
 * @since 2025-11-14
 */
@Schema(description = "APP 文案配置")
@TableName("app_doc")
class AppDoc : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "文案ID", example = "1", nullable = true)
    var id: Long? = null

    /**
     * 用途
     */
    @TableField("used_for")
    @Schema(description = "文案用途标识，例如 home_popup", example = "home_popup", nullable = true)
    var usedFor: String? = null

    /**
     * 国际化标识
     */
    @TableField("i18n_code")
    @Schema(description = "国际化标识编码", example = "doc_register_tip", nullable = true)
    var i18nCode: String? = null

    /**
     * 标题
     */
    @TableField("title")
    @Schema(description = "文案标题", example = "欢迎使用", nullable = true)
    var title: String? = null

    /**
     * 内容
     */
    @TableField("content")
    @Schema(description = "文案内容（支持 HTML）", example = "<p>欢迎加入平台</p>", nullable = true)
    var content: String? = null

    /**
     * 资源地址路径
     */
    @TableField("source_uri")
    @Schema(description = "资源路径或跳转链接", example = "/docs/register", nullable = true)
    var sourceUri: String? = null

    /**
     * 排序，值越大越靠前
     */
    @TableField("sort_by")
    @Schema(description = "排序值，越大越靠前", example = "100", nullable = true)
    var sortBy: Int? = null

    /**
     * 创建人
     */
    @TableField("create_by")
    @Schema(description = "创建人", example = "admin", nullable = true)
    var createBy: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2025-11-14T10:00:00", nullable = true)
    var createTime: LocalDateTime? = null

    /**
     * 更新人
     */
    @TableField("update_by")
    @Schema(description = "更新人", example = "editor", nullable = true)
    var updateBy: String? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2025-11-15T08:30:00", nullable = true)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppDoc{" +
        "id=" + id +
        ", usedFor=" + usedFor +
        ", i18nCode=" + i18nCode +
        ", title=" + title +
        ", content=" + content +
        ", sourceUri=" + sourceUri +
        ", sortBy=" + sortBy +
        ", createBy=" + createBy +
        ", createTime=" + createTime +
        ", updateBy=" + updateBy +
        ", updateTime=" + updateTime +
        "}"
    }
}
