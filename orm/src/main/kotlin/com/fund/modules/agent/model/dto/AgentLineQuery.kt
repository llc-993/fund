package com.fund.modules.agent.model.dto

import com.fund.common.entity.PageReq


/**
 * 直属下级分页查询
 */
class AgentLineQuery: PageReq() {

    /**
     * 上级Id
     */
    var proxyId: Long? = null

    /**
     * 用户名
     */
    var userName: String? = null

}
