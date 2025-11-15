package com.fund.modules.banner.model;

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
 * app banner 轮播图
 * </p>
 *
 * @author 书记
 * @since 2025-11-14
 */
@Schema(description = "APP轮播图配置")
@TableName("app_banner")
class AppBanner : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "banner主键ID", example = "1", nullable = true)
    var id: Long? = null

    /**
     * 跳转类型：1：外部跳转 2：内部跳转
     */
    @TableField("skip_type")
    @Schema(description = "跳转类型（1=外部，2=内部）", example = "1", nullable = true)
    var skipType: Int? = null

    /**
     * 国际化标识
     */
    @TableField("i18n_code")
    @Schema(description = "国际化标识编码（可选）", example = "banner_home_top", required = false, nullable = true)
    var i18nCode: String? = null

    /**
     * 资源URL
     */
    @TableField("img_url")
    @Schema(description = "图片资源URL", example = "https://cdn.example.com/banner1.png", nullable = true)
    var imgUrl: String? = null

    /**
     * 跳转URL
     */
    @TableField("skip_url")
    @Schema(description = "跳转链接", example = "https://example.com/activity/1", nullable = true)
    var skipUrl: String? = null

    /**
     * 广告内容描述
     */
    @TableField("content")
    @Schema(description = "广告内容描述", example = "限时理财活动", nullable = true)
    var content: String? = null

    /**
     * 0：关闭 1：开启
     */
    @TableField("banner_status")
    @Schema(description = "状态（0=关闭，1=开启）", example = "1", nullable = true)
    var bannerStatus: Boolean? = null

    /**
     * 排序
     */
    @TableField("sort_by")
    @Schema(description = "排序值，越大越靠前", example = "10", nullable = true)
    var sortBy: Int? = null

    /**
     * 创建人
     */
    @TableField("create_by")
    @Schema(description = "创建人", example = "admin", nullable = true)
    var createBy: String? = null

    /**
     * 更新人
     */
    @TableField("update_by")
    @Schema(description = "更新人", example = "editor", nullable = true)
    var updateBy: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2025-11-14T10:00:00", nullable = true)
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2025-11-15T08:30:00", nullable = true)
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "AppBanner{" +
        "id=" + id +
        ", skipType=" + skipType +
        ", i18nCode=" + i18nCode +
        ", imgUrl=" + imgUrl +
        ", skipUrl=" + skipUrl +
        ", content=" + content +
        ", bannerStatus=" + bannerStatus +
        ", sortBy=" + sortBy +
        ", createBy=" + createBy +
        ", updateBy=" + updateBy +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}"
    }
}
