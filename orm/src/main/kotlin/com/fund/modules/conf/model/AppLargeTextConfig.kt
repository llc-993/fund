package com.fund.modules.conf.model;

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * app配置(大文本)
 * </p>
 *
 * @author 书记
 * @since 2025-10-19
 */
@TableName("app_large_text_config")
class AppLargeTextConfig : Serializable {

    /**
     * id
     */
    @TableField("id")
    var id: Long? = null

    /**
     * 标识
     */
    @TableField("code")
    var code: String? = null

    /**
     * 分组ID 默认0.具体含义根据业务推断
     */
    @TableField("group_id")
    var groupId: Int? = null

    /**
     * 排序字段，越小越靠前
     */
    @TableField("sort_level")
    var sortLevel: Int? = null

    /**
     * 国际化标识，可能没有
     */
    @TableField("i18n_code")
    var i18nCode: String? = null

    /**
     * 链接，可能是图片
     */
    @TableField("link")
    var link: String? = null

    /**
     * 标题，可能没有标题
     */
    @TableField("title")
    var title: String? = null

    /**
     * 值(大文本)
     */
    @TableField("value")
    var value: String? = null

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
        return "AppLargeTextConfig{" +
        "id=" + id +
        ", code=" + code +
        ", groupId=" + groupId +
        ", sortLevel=" + sortLevel +
        ", i18nCode=" + i18nCode +
        ", link=" + link +
        ", title=" + title +
        ", value=" + value +
        ", createBy=" + createBy +
        ", createTime=" + createTime +
        ", updateBy=" + updateBy +
        ", updateTime=" + updateTime +
        "}"
    }
}
