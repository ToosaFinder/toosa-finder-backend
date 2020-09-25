package com.toosafinder.api

import kotlinx.coroutines.*
import kotlinx.coroutines.promise
import kotlin.js.Promise

@JsExport
actual abstract class ApiMethod<T, R> actual constructor(
        protected actual val req: T
) {

    internal actual abstract suspend fun executeInternal(): R

    @JsName("execute")
    fun execute(): Promise<R> =
        GlobalScope.promise { executeInternal() }
}
