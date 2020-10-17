package com.toosafinder.webcommon

import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength

/**
 * @throws ValidationException если запрос не прошел валидацию для последующей обработки в [ExceptionHandler].
 */
fun <T> Validation<T>.throwIfNotValid(req: T): Unit =
    when(val res = this.validate(req)){
        is Valid -> {}
        is Invalid -> {
            val errors = res.errors.map {
                it.dataPath to it.message
            }.toMap()
            throw ValidationException(errors)
        }
    }

/**
 * содержит список ошибок в виде путь->сообщение
 */
class ValidationException(
    val errors: Map<String, String>
): RuntimeException()

/**
 * Содержит список часто используемых валидаций
 */
class Validations {

    companion object {
        val passwordValidation = Validation<String> {
            minLength(8)
            maxLength(320)
        }


    }
}