package com.toosafinder.security.registration

import com.toosafinder.api.registration.UserRegistrationErrors
import com.toosafinder.api.registration.UserRegistrationReq
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.webcommon.HTTP
import com.toosafinder.webcommon.throwIfNotValid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user/registration")
private class RegistrationController(
    private val registrationService: DummyRegistrationService
) {

    private val log by LoggerProperty()

    val validation = Validation<UserRegistrationReq> {
        UserRegistrationReq::email {
            minLength(2)
            maxLength(320)
        }

        UserRegistrationReq::password {
            minLength(8)
        }
    }

    @PutMapping
    fun registerUser(@RequestBody req: UserRegistrationReq): ResponseEntity<*> {
        validation.throwIfNotValid(req)
        log.trace("trying to register $req")
        return when(registrationService.registerUser(req.email, req.password)){
            is RegResult.Success -> HTTP.ok<Unit>()
            is RegResult.LoginDuplication -> HTTP.conflict(
                code = UserRegistrationErrors.LOGIN_DUPLICATION_ERROR.name,
                message = "optional message here",
                payload = "some additional info here"
            )
            is RegResult.EmailDuplication -> HTTP.conflict(
                code = UserRegistrationErrors.EMAIL_DUPLICATION_ERROR.name,
            )
        }
    }

}

private sealed class RegResult {
    object Success: RegResult()
    object LoginDuplication: RegResult()
    object EmailDuplication: RegResult()
}

@Service
private class DummyRegistrationService {

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun registerUser(email: String, password: String): RegResult = RegResult.LoginDuplication

}