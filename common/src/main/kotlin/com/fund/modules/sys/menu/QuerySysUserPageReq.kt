package com.fund.modules.sys.menu

import com.fund.common.entity.PageReq
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "查询系统用户分页请求参数")
class QuerySysUserPageReq : PageReq() {
    
    @Schema(description = "部门ID", example = "1")
    var deptId: Long? = null
}
