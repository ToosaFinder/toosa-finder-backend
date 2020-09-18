package com.toosafinder.webcommon

import com.toosafinder.api.UserCredentials
import io.konform.validation.Invalid
import io.konform.validation.ValidationErrors
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object HTTP {
    fun <T> ok(): ResponseEntity<T> = ResponseEntity.ok().build()
    fun <T> conflict(): ResponseEntity<T> = ResponseEntity.status(HttpStatus.CONFLICT).build()
    fun badRequest(validationResult: Invalid<UserCredentials>): ResponseEntity<ValidationErrors> =
        ResponseEntity.badRequest().body(validationResult.errors)
}