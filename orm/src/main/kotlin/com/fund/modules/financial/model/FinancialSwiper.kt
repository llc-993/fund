package com.fund.modules.financial.model;

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
 * 理财轮播图
 * </p>
 *
 * @author 书记
 * @since 2025-10-27
 */
@Schema(description = "理财轮播图实体")
@TableName("financial_swiper")
class FinancialSwiper : Serializable {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * 标题
     */
    @Schema(description = "标题")
    @TableField("title")
    var title: String? = null

    /**
     * 轮播图url
     */
    @Schema(description = "轮播图URL")
    @TableField("img_url")
    var imgUrl: String? = null

    /**
     * 是否链接(1-否 2-是)
     */
    @Schema(description = "是否链接(1-否 2-是)")
    @TableField("is_link")
    var isLink: String? = null

    /**
     * 链接类型(1-内部链接 2-外部链接)
     */
    @Schema(description = "链接类型(1-内部 2-外部)")
    @TableField("link_type")
    var linkType: String? = null

    /**
     * 链接url
     */
    @Schema(description = "链接地址")
    @TableField("link_url")
    var linkUrl: String? = null

    /**
     * 是否打开新Tab(1-否 2-是)
     */
    @Schema(description = "是否新标签打开(1-否 2-是)")
    @TableField("is_new_tab")
    var isNewTab: String? = null

    /**
     * 排序
     */
    @Schema(description = "排序值，越大越靠前")
    @TableField("sort")
    var sort: Int? = null

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    @TableField("create_by")
    var createBy: String? = null

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    /**
     * 修改者
     */
    @Schema(description = "修改者")
    @TableField("update_by")
    var updateBy: String? = null

    override fun toString(): String {
        return "FinancialSwiper(id=$id, title=$title, imgUrl=$imgUrl, isLink=$isLink, linkType=$linkType, linkUrl=$linkUrl, isNewTab=$isNewTab, sort=$sort, createTime=$createTime, createBy=$createBy, updateTime=$updateTime, updateBy=$updateBy)"
    }
}
