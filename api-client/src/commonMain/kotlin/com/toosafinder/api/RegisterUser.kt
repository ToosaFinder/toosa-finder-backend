package com.toosafinder.api

import kotlin.js.JsExport
import kotlin.js.JsName


@JsExport
data class RegisterUserReq(
    @JsName("email")
    val email: String,
    @JsName("login")
    val login: String,
    @JsName("password")
    val password: String
)

@JsExport
interface RegisterUserRes {
    class Success: RegisterUserRes
    class EmailExists: RegisterUserRes
    class LoginExists: RegisterUserRes
}

@JsExport
class RegisterUser: ApiMethod<RegisterUserReq, RegisterUserRes>(){

    override suspend fun invokeInternal(arg: RegisterUserReq): RegisterUserRes {
        return RegisterUserRes.Success()
    }

}