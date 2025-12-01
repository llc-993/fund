package com.fund.modules.news.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 股票新闻表
 * </p>
 *
 * @author 书记
 * @since 2025-11-28
 */
@Schema(description = "股票新闻表")
@TableName("stock_news")
class StockNews : Serializable {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "8", nullable = true)
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 新闻标题
     */
    @Schema(description = "新闻标题", example = "新闻标题", nullable = true)
    @TableField("title")
    var title: String? = null

    /**
     * 新闻来源
     */
    @Schema(description = "新闻来源", nullable = true)
    @TableField("provider")
    var provider: String? = null

    /**
     * 新闻链接
     */
    @JsonIgnore
    @Schema(description = "新闻链接")
    @TableField("link")
    var link: String? = null

    /**
     * 新闻详情内容
     */
    @Schema(description = "新闻详情内容", example = "<p>新闻详情内容<p/>", nullable = true)
    @TableField("content")
    var content: String? = null

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
        return "StockNews{" +
                "id=" + id +
                ", title=" + title +
                ", provider=" + provider +
                ", link=" + link +
                ", content=" + content +
                ", createBy=" + createBy +
                ", createTime=" + createTime +
                ", updateBy=" + updateBy +
                ", updateTime=" + updateTime +
                "}"
    }
}
