package com.fund.common.entity


/**
 * 分页参数
 */
open class PageReq {

    /**
     * 第几页
     */
    var pageNum: Long = 1

    // 每页条数
    /**
     * 每页条数
     */
    var pageSize: Long = 10
}
