package com.toosafinder.api

expect abstract class ApiMethod<T, R>(
    req: T
) {
    protected val req: T
    internal abstract suspend fun executeInternal(): R
}