package com.fund.utils

import cn.hutool.core.util.StrUtil

object ImageUtil {
    private val imagesSuffix = StrUtil.splitTrim(
        ".jpg|.bmp|.eps|.gif|.mif|.miff|.png|.tif|.tiff|.svg|.wmf|.jpe|.jpeg|.dib|.ico|.tga|.cut|.pic",
        "|"
    )

    fun isImage(target: String?): Boolean {
        var rs = false
        for (end in imagesSuffix) {
            rs = target!!.endsWith(end)
            if (rs) {
                break
            }
        }
        return rs
    }
}