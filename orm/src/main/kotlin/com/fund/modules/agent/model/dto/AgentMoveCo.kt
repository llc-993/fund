package com.fund.modules.agent.model.dto

import jakarta.validation.constraints.NotBlank

/**
 * 代理线迁移参数实体
 */
class AgentMoveCo {

    /**
     * 迁移者
     */
    var fromUserAccount: @NotBlank(message = "迁移者会员账号不能为空") String? = null

    /**
     * 接收者
     */
    var toUserAccount: @NotBlank(message = "接收者会员账号不能为空") String? = null
}
