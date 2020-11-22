package com.toosafinder.api.login

data class PasswordSetReq(
    val emailToken: String,
    val password: String
)

enum class PasswordSetErrors {
    EMAIL_TOKEN_NOT_VALID
}