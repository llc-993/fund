package com.fund.modules.sys.menu

import com.fund.common.entity.PageReq
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "查询操作日志分页请求参数")
class QueryOptLogReq: PageReq() {

    @Schema(description = "备注信息（支持模糊搜索）", example = "用户登录")
    var remark: String? = null

}
