package com.fund.exception

/**
 * A custom runtime exception for business logic errors, with an associated error code.
 *
 * @property code The error code associated with the exception (default: 500).
 * @property message The error message (optional).
 * @property cause The underlying cause of the exception (optional).
 */
import java.util.function.Supplier

/**
 * 业务异常
 */
class BusinessException: RuntimeException {

    var code: Int = 500

    companion object {
        @JvmStatic
        fun build(message: String): Supplier<BusinessException> {
            return Supplier { BusinessException(message) }
        }
    }

    constructor(): super()

    constructor(code: Int, message: String?, th: Throwable): super(message, th) {
        this.code = code
    }

    constructor(code: Int, message: String) : super(message) {
        this.code = code
    }

    constructor(message: String, th: Throwable) : super(message, th)

    constructor(message: String): super(message)

    constructor(th: Throwable): super(th)

}
