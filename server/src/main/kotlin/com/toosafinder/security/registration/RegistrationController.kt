package com.toosafinder.security.registration

import com.toosafinder.api.registration.UserRegistrationErrors
import com.toosafinder.api.registration.UserRegistrationReq
import com.toosafinder.email.EmailSendingResult
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.security.UserCreationResult
import com.toosafinder.security.UserService
import com.toosafinder.security.email.SecurityEmailService
import com.toosafinder.webcommon.HTTP
import com.toosafinder.webcommon.Validations
import com.toosafinder.webcommon.throwIfNotValid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user/registration")
private class RegistrationController(
    private val registrationService: RegistrationService
) {

    private val log by LoggerProperty()

    val validation = Validation<UserRegistrationReq> {
        UserRegistrationReq::email {
            minLength(2)
            maxLength(320)
        }

        UserRegistrationReq::password {
            run(Validations.passwordValidation)
        }
    }

    @PostMapping
    fun registerUser(@RequestBody req: UserRegistrationReq): ResponseEntity<*> {
        log.trace("Trying to register user with email \"${req.email}\"")
        validation.throwIfNotValid(req)
        return when (registrationService.registerUser(req.login, req.email, req.password)) {
            is RegResult.Success -> HTTP.ok()
            is RegResult.LoginDuplication -> HTTP.conflict(
                code = UserRegistrationErrors.LOGIN_DUPLICATION_ERROR.name,
                message = "Provided username is already in use"
            )
            is RegResult.EmailDuplication -> HTTP.conflict(
                code = UserRegistrationErrors.EMAIL_DUPLICATION_ERROR.name,
                message = "Provided email is already in use"
            )
        }
    }

}

@Service
private class RegistrationService(
        private val securityEmailService: SecurityEmailService,
        private val emailTokenService: EmailTokenService,
        private val userService: UserService
) {

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun registerUser(login: String?, email: String, password: String): RegResult {
        val user = when (val userCreationResult = userService.createUser(login, email, password)) {
            is UserCreationResult.Success -> userCreationResult.user
            is UserCreationResult.EmailDuplication -> return RegResult.EmailDuplication
            is UserCreationResult.LoginDuplication -> return RegResult.LoginDuplication
        }

        val emailToken = emailTokenService.generateToken(user)
        when (securityEmailService.sendEmailConfirmation(email, emailToken.value)) {
            is EmailSendingResult.Success -> return RegResult.Success
            is EmailSendingResult.SendingError -> throw RuntimeException("Failed to send email confirmation message")
        }
    }

}

private sealed class RegResult {
    object Success: RegResult()
    object LoginDuplication: RegResult()
    object EmailDuplication: RegResult()
}
