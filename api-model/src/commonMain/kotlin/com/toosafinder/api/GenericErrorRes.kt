package com.toosafinder.api

data class GenericErrorRes(
    val code: GenericErrorCode,
    val message: String,
    val payload: Any? = null
)

enum class GenericErrorCode {
    /**
     * Необработанное исключение на сервере. message берется из исключения
     */
    GENERIC_SERVER_ERROR,

    /**
     * Необработанное исключение на сервере, возникшее при работе с электронной почтой.
     * message берется из исключения
     */
    EMAIL_ERROR
}