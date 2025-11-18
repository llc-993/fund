package com.fund.modules.auth

class AdminLoginUser: AuthUser()  {

    var username: String? = null

    var deptId: Long? = null

    var roleId: Long? = null

    var roles: List<String>? = null

    var oriShareCode: String? = null
}