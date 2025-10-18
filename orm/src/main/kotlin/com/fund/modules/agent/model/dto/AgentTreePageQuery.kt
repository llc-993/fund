package com.fund.modules.agent.model.dto

import com.fund.common.entity.PageReq


/**
 * 代理关系树查询-分页
 */
class AgentTreePageQuery: PageReq() {
    /**
     * 上级id
     */
    var proxyId: Long? = null

    /**
     * 会员账号
     */
    var userName: String? = null

    /**
     * 上级账号
     */
    var proxyUserName: String? = null

    /**
     * 用户邀请码
     */
    var oriShareCode: String? = null

    /**
     * 上级邀请码
     */
    var proxyShareCode: String? = null
}
