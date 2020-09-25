package com.toosafinder.api

actual abstract class ApiMethod<T, R> actual constructor(
        protected actual val req: T) {
    internal actual abstract suspend fun executeInternal(): R

    suspend fun call(): R = executeInternal()
}