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
 * 代理层级关联表
 * </p>
 *
 * @author 书记
 * @since 2025-08-21
 */
@TableName("app_agent_relation")
class AppAgentRelation : Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null

    /**
     * (源)用户ID
     */
    @TableField("ori_user_id")
    var oriUserId: Long? = null

    /**
     * (源)用户邀请码
     */
    @TableField("ori_share_code")
    var oriShareCode: String? = null

    /**
     * (源)用户账号
     */
    @TableField("ori_account")
    var oriAccount: String? = null

    /**
     * 总代用户ID
     */
    @TableField("top_user_id")
    var topUserId: Long? = null

    /**
     * 总代用户邀请码
     */
    @TableField("top_share_code")
    var topShareCode: String? = null

    /**
     * 级别: (0)-总代 (1)-一级代理 (2)-二级代理 (3-无限)-会员 
     */
    @TableField("level")
    var level: Int? = null

    /**
     * 用户组 1正式组 0测试组
     */
    @TableField("user_group")
    var userGroup: Int? = null

    /**
     * 一级代理用户id(直属上级)
     */
    @TableField("p1_id")
    var p1Id: Long? = null

    /**
     * 一级代理邀请码(直属上级)
     */
    @TableField("p1_code")
    var p1Code: String? = null

    /**
     * 一级代理账号(直属上级)
     */
    @TableField("p1_account")
    var p1Account: String? = null

    /**
     * 二级代理用户id(直属上级)
     */
    @TableField("p2_id")
    var p2Id: Long? = null

    /**
     * 二级代理邀请码(直属上级)
     */
    @TableField("p2_code")
    var p2Code: String? = null

    /**
     * 二级代理账号(直属上级)
     */
    @TableField("p2_account")
    var p2Account: String? = null

    /**
     * 三级代理用户id(直属上级)
     */
    @TableField("p3_id")
    var p3Id: Long? = null

    /**
     * 三级代理邀请码(直属上级)
     */
    @TableField("p3_code")
    var p3Code: String? = null

    /**
     * 三级代理账号(直属上级)
     */
    @TableField("p3_account")
    var p3Account: String? = null

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
        return "AppAgentRelation{" +
        "id=" + id +
        ", oriUserId=" + oriUserId +
        ", oriShareCode=" + oriShareCode +
        ", oriAccount=" + oriAccount +
        ", topUserId=" + topUserId +
        ", topShareCode=" + topShareCode +
        ", level=" + level +
        ", userGroup=" + userGroup +
        ", p1Id=" + p1Id +
        ", p1Code=" + p1Code +
        ", p1Account=" + p1Account +
        ", p2Id=" + p2Id +
        ", p2Code=" + p2Code +
        ", p2Account=" + p2Account +
        ", p3Id=" + p3Id +
        ", p3Code=" + p3Code +
        ", p3Account=" + p3Account +
        ", createBy=" + createBy +
        ", createTime=" + createTime +
        ", updateBy=" + updateBy +
        ", updateTime=" + updateTime +
        "}"
    }
}
