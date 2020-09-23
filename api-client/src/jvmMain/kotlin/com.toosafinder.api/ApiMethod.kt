package com.toosafinder.api

actual abstract class ApiMethod<T, R> {
    internal actual abstract suspend fun invokeInternal(arg: T): R
    suspend fun invoke(arg: T): R = invokeInternal(arg)
}