package com.toosafinder.security.login

import com.toosafinder.api.login.*
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
    private val passwordRestoreService: PasswordRestoreService,
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

        return when(passwordRestoreService.restorePassword(req.email)) {
            is PasswordRestoreResult.Success -> HTTP.ok()
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
        return when(passwordRestoreService.setPassword(req.emailToken, req.password)) {
            is PasswordSetResult.Success -> HTTP.ok()
            is PasswordSetResult.TokenNotValid -> HTTP.conflict(
                code = PasswordSetErrors.EMAIL_TOKEN_NOT_VALID.name
            )
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody req: LoginReq): ResponseEntity<*>{
        return when(val res = loginService.login(req.userId, req.password)){
            is LoginResult.Failure -> HTTP.conflict(code = LOGIN_ERROR)
            is LoginResult.Success -> HTTP.ok(
                LoginRes(
                    accessToken = res.accessToken
                ))
        }
    }

}