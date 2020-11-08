package com.toosafinder.api

data class ErrorRes(
    val code: String,
    val message: String? = null,
    val payload: Any? = null
)

data class ValidationError(
    val errors: Map<String, String>
)