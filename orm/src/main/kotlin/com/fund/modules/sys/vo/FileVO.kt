package com.fund.modules.sys.vo

class FileVO {

    /**
     * 文件上传到服务器的相对路径， 访问文件: 文件下载域名 + ‘/’ + 相对路径
     */
    var filePath: String? = null

    /**
     * 文件下载域名
     */
    var fileHost: String? = null
}