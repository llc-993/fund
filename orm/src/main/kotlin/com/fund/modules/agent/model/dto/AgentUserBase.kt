package com.fund.modules.agent.model.dto

import java.util.Date


class AgentUserBase: AgentBase() {

    /**
     * 注册时间
     */
    var regTime: Date? = null

    /**
     * 下级总数量 一级
     */
    var lineQty: Long? = null

}
