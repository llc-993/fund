package com.fund.modules.doc.enums

import com.fund.common.dto.Label


enum class UsedForEnum(var label:String , var value:String) {
    ABOUT_US("关于我们", "about_us"),
    TERM_AND_CONDITIONS("条款和条件", "term_and_conditions"),
    EVENTS("最新事件", "events"),
    FAQ("常见问题","faq"),
    CERTIFICATE("证书", "certificate"),
    //HOME_ALERT("首页弹窗", "home_alert"),
    TASK_DOC("抢单文案", "task_doc"),
    REG_AGREE("注册协议", "reg_agree"),
    SIGN_IN_RESULT("签到规则", "sign_in_result"),
    SALARY("工资图","salary")
    ;

    companion object {
        fun toLabel(): List<Label<String, String>> {
            return entries.map { Label(it.value, it.label) }.toList()
        }
    }

}
