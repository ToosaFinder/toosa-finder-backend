package com.toosafinder.api.registration

data class UserRegistrationReq(
    val email: String,
    val login: String?,
    val password: String
)

enum class UserRegistrationErrors {
    LOGIN_DUPLICATION_ERROR,
    EMAIL_DUPLICATION_ERROR
}
