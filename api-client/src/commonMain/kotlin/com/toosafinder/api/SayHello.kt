package com.toosafinder.api

import kotlinx.coroutines.delay
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class SayHello: ApiMethod<HelloReq, HelloRes>() {

    var i = 0

    override suspend fun invokeInternal(arg: HelloReq): HelloRes =
        if(i % 2 == 0) {
            delay(5000)
            HelloRes.Success("Hello, ${arg.name}")
        } else{
            HelloRes.Error("sorry")
        }

}

@JsExport
data class HelloReq (
    @JsName("name")
    val name: String
)

@JsExport
sealed class HelloRes{
    data class Success(
        @JsName("hello") val hello: String): HelloRes()
    data class Error(
        @JsName("message") val message: String): HelloRes()
}

