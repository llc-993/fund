package com.fund.common.entity

import com.fund.common.Constants


data class R<T> (val data:T?, val msg:String,val code:Int){

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
