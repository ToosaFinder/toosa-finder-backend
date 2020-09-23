package com.toosafinder.api

expect abstract class ApiMethod<T, R>() {
    internal abstract suspend fun invokeInternal(arg: T): R
}