package com.toosafinder.api.login

data class PasswordRestoreReq (
    val email: String
)

enum class PasswordRestoreErrors {
    USER_NOT_FOUND,
    EMAIL_SENDING_ERROR
}

