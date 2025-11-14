package com.fund.modules.user


import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@Schema(description = "kyc参数")
class UserUpdateRequest: Serializable {

    /**
     * 身份信息正面
     */

    @Schema(description = "身份信息正面")
    var kycPic1: String? = null

    /**
     * 身份信息反面
     */

    @Schema(description = "身份信息反面")
    var kycPic2: String? = null

    /**
     * 身份信息反面
     */
    @Schema(description = "身份ID号")
    var idNumber: String? = null

}