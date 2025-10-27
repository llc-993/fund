package com.fund.common.entity

import com.fund.common.Constants
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "统一响应结果")
data class R<T> (
    @Schema(description = "响应数据", nullable = true)
    val data: T?,
    
    @Schema(description = "响应消息", example = "success")
    val msg: String,
    
    @Schema(description = "响应状态码", example = "200")
    val code: Int
) {

    companion object {

        fun <T> success(msg: String = Constants.success, code: Int = HttpStatus.SUCCESS): R<T> {
            return success(null, msg, code)
        }

        fun <T> success(data: T?, msg: String = Constants.success, code: Int = HttpStatus.SUCCESS): R<T> {
            return R(data, msg, code)
        }

        fun <T> error(msg: String = Constants.fail, code: Int = HttpStatus.ERROR): R<T> {
            return R(null, msg, code)
        }
    }

    class HttpStatus {
        companion object {
            /**
             * 操作成功
             */
            const val SUCCESS = 200

            /**
             * 系统内部错误
             */
            const val ERROR = 500

        }
    }
}
