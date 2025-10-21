package com.fund.modules.conf.dto


/**
 * 邮件模板
 */
class EmailTemplate {

    /**
     * 是否启用
     */
    var enable: Boolean? = null

    /**
     * 主题
     */
    var subject: String? = null

    /**
     * 邮件内容
     */
    var htmlBody: String? = null

}