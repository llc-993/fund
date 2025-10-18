package com.fund.modules.agent.model.dto

import com.fasterxml.jackson.annotation.JsonIgnore


open class AgentBase {
    /**
     * (源)用户ID
     */
    var oriUserId: Long? = null

    /**
     * (源)用户邀请码
     */
    var oriShareCode: String? = null

    /**
     * (源)用户账号
     */
    var oriAccount: String? = null


    /**
     * 一级代理用户id(直属上级)
     */
    @JsonIgnore
    var p1Id: Long? = null

    /**
     * 真实姓名
     */
    var realName: String? = null
}
