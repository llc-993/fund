package com.fund.common.entity

import jakarta.validation.constraints.NotNull


class EnableReq {

    var id: @NotNull Long? = null

    /**
     * 是否启用
     */
    var enable: @NotNull Boolean? = null
}
