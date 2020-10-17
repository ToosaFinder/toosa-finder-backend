package com.toosafinder.security.login

import com.toosafinder.api.login.PasswordRestoreErrors
import com.toosafinder.api.login.PasswordRestoreReq
import com.toosafinder.api.login.PasswordSetErrors
import com.toosafinder.api.login.PasswordSetReq
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.webcommon.HTTP
import com.toosafinder.webcommon.Validations
import com.toosafinder.webcommon.throwIfNotValid
import io.konform.validation.Validation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
private class LoginController(
    private val loginService: LoginService
) {

    private val log by LoggerProperty()

    private val passwordSetValidation = Validation<PasswordSetReq> {
        PasswordSetReq::password {
            run(Validations.passwordValidation)
        }
    }

    @PostMapping("/restore-password")
    fun restorePassword(@RequestBody req: PasswordRestoreReq): ResponseEntity<*> {
        log.trace("User with email ${req.email} has queried password restore")

        return when(loginService.restorePassword(req.email)) {
            is PasswordRestoreResult.Success -> HTTP.ok<Unit>()
            is PasswordRestoreResult.UserNotFound -> HTTP.conflict(
                code = PasswordRestoreErrors.USER_NOT_FOUND.name
            )
            is PasswordRestoreResult.EmailSendingError -> HTTP.conflict(
                code = PasswordRestoreErrors.EMAIL_SENDING_ERROR.name
            )
        }
    }

    @PostMapping("/set-password")
    fun setPassword(@RequestBody req: PasswordSetReq): ResponseEntity<*> {
        log.trace("User with email token ${req.emailToken} has queried password change")

        passwordSetValidation.throwIfNotValid(req)
        return when(loginService.setPassword(req.emailToken, req.password)) {
            is PasswordSetResult.Success -> HTTP.ok<Unit>()
            is PasswordSetResult.TokenNotValid -> HTTP.conflict(
                code = PasswordSetErrors.EMAIL_TOKEN_NOT_VALID.name
            )
        }
    }
}