package com.fund.modules.user

import com.fund.common.entity.PageReq


class AdminUserQueryReq: PageReq() {

    var username: String? = null

    var userAccount : String? = null

    /**
     * 手机号
     */
    var mobilePhone: String? = null



}