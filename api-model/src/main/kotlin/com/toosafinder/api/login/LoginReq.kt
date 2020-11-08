package com.toosafinder.api.login

const val LOGIN_ERROR = "LOGIN_ERROR"

data class LoginReq(
    /**
     * это может быть логин или емейл
     */
    val userId: String,
    val password: String
)

data class LoginRes(
    val accessToken: String,
    val refreshToken: String? = null
)