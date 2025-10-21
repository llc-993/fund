package com.fund.modules.conf.dto

import com.fund.modules.conf.ant.DefaultValue

/**
 * google邮箱配置
 */
class GmailConfig {

    /**
     * host
     */
    @DefaultValue("smtp.gmail.com")
    var smtpHost: String? = null

    /**
     * gmailUsername
     */
    var gmailUsername: String? = null

    /**
     * gmailPassword
     */
    var gmailPassword: String? = null

    /**
     * 谷歌邮箱是否开启调试模式
     */
    @DefaultValue("false")
    var gmailDebug: Boolean? = null
}
