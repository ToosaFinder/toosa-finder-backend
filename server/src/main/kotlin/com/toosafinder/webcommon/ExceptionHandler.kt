package com.toosafinder.webcommon

import com.toosafinder.api.GenericErrorCode
import com.toosafinder.api.GenericErrorRes
import com.toosafinder.api.ValidationError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailException
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Service
@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(e: ValidationException): ResponseEntity<ValidationError>{
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ValidationError(e.errors))
    }

    @ExceptionHandler(MailException::class)
    fun handleEmailException(e: MailException): ResponseEntity<GenericErrorRes> =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(GenericErrorRes(
                code = GenericErrorCode.EMAIL_ERROR,
                message = e.message ?: e.cause?.message ?: "Unknown email error"
            ))

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(e: Throwable): ResponseEntity<GenericErrorRes>
        = ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(GenericErrorRes(
                    code = GenericErrorCode.GENERIC_SERVER_ERROR,
                    message = e.message ?: e.cause?.message ?: "unreadable error",
                    payload = null
                ))

}
