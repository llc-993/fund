package com.fund.modules.conf.dto

/**
 * 邮件模板配置
 */
class EmailTemplateConfig {

    /**
     * 注册验证码
     */
    var register: EmailTemplate? = null

    /**
     * 提现审核成功通知
     */
    var cashOutReview: EmailTemplate? = null

    /**
     * 后台上分通知
     */
    var cashAdd: EmailTemplate? = null

    /**
     * 后台下分通知
     */
    var cashSub: EmailTemplate? = null

}