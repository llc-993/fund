package com.fund.modules.user

import com.fund.common.entity.PageReq
import java.io.Serializable


class AdminUserQueryReq: PageReq(), Serializable {

    var userName: String? = null

    var userAccount : String? = null

    /**
     * 手机号
     */
    var mobilePhone: String? = null



}