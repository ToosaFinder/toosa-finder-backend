package com.toosafinder.api

import com.toosafinder.api.auth.TokenStorage
import io.ktor.client.*
import kotlin.js.JsExport
import kotlin.js.JsName


@JsExport
class Api internal constructor(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
) {

    @JsName("isLoggedIn")
    fun isLoggedIn(): Boolean =
            tokenStorage.isLoggedIn()

    @JsName("registerUser")
    fun registerUser(email: String, login: String, password: String) =
            RegisterUser(RegisterUserReq(email, login, password), httpClient)
}

@JsExport
abstract class LocalStorage {
    @JsName("set")
    abstract fun set(key: String, value: String)
    @JsName("get")
    abstract fun get(key: String): String?
    @JsName("remove")
    abstract fun remove(key: String): String?
}

@JsExport
@JsName("createApi")
fun createApi(
        baseUrl: String,
        localStorage: LocalStorage,
        onLogIn: () -> Unit,
        onLogOut: () -> Unit,
        onNetworkError: (message: String) -> Unit,
        onServerError: (message: String) -> Unit
): Api {

    val tokenStorage = TokenStorage(localStorage, onLogIn, onLogOut)

    val http = HttpClient {

//        defaultRequest {
//            url.takeFrom(Url(baseUrl))
//            contentType(ContentType.Application.Json)
//        }
//
//        install(JsonFeature)
//        install("setAuth"){
//            tokenStorage.accessToken?.let {
//                headersOf(ACCESS_TOKEN_KEY, it)
//            } ?: onLogOut()
//        }
//
//        HttpResponseValidator {
//            validateResponse { response ->
//                val statusCode = response.status.value
//                val originCall = response.call
//                if (statusCode < 300 || originCall.attributes.contains(ValidateMark)) return@validateResponse
//
//                val exceptionCall = originCall.save().apply {
//                    attributes.put(ValidateMark, Unit)
//                }
//
//                val exceptionResponse = exceptionCall.response
//                when (statusCode) {
//                    in 300..399 -> throw RedirectResponseException(exceptionResponse)
//                    in 400..499 -> throw ClientRequestException(exceptionResponse)
//                    in 500..599 -> throw ServerResponseException(exceptionResponse)
//                    else -> throw ResponseException(exceptionResponse)
//                }
//            }
//        }
//        install("checkAuthStatus"){
//        }
    }
    return Api(http, tokenStorage)
}

