package com.toosafinder.api

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

@JsExport
interface Promisable<T, R>{
    @JsName("promise")
    fun promise(arg: T): Promise<R>
}

@JsExport
actual abstract class ApiMethod<T, R>: Promisable<T,R> {
    internal actual abstract suspend fun invokeInternal(arg: T): R
    final override fun promise(arg: T): Promise<R> =
        GlobalScope.promise { invokeInternal(arg) }
}