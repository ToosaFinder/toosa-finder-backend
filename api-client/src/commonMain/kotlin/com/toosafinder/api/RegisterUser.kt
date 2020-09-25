package com.toosafinder.api

import io.ktor.client.*
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
class RegisterUser(
    req: RegisterUserReq,
    private val http: HttpClient
): ApiMethod<RegisterUserReq, RegisterUserRes>(req){

    override suspend fun executeInternal(): RegisterUserRes {
//        val res = http.post<com.toosafinder.api.RegisterUserRes>("/user/registerUser"){
//            body = req
//        }

        return RegisterUserRes.Success()
    }

}