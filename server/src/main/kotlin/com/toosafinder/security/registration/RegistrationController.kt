package com.toosafinder.security.registration

import com.toosafinder.api.UserCredentials
import com.toosafinder.webcommon.HTTP
import io.konform.validation.Invalid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@RestController
@RequestMapping("/user/registration")
private class RegistrationController(
    private val registrationService: RegistrationService
) {

    val validateUser = Validation<UserCredentials> {
        UserCredentials::email {
            minLength(2)
            maxLength(320)
        }

        UserCredentials::password {
            minLength(8)
        }
    }

    @PutMapping
    fun registerUser(@RequestBody userCredentials: UserCredentials): ResponseEntity<*> {
        when(val validationResult = validateUser(userCredentials)){
            is Invalid -> return HTTP.badRequest(validationResult)
        }

        return when(registrationService.registerUser(userCredentials.email, userCredentials.password)){
            is RegResult.Success -> HTTP.ok<Unit>()
            is RegResult.AlreadyExists -> HTTP.conflict<Unit>()
        }
    }

    @GetMapping
    fun getUsersCredentials(): List<UserCredentials> =
        registrationService.getAllUsersCredentials()

}

private sealed class RegResult {
    object Success: RegResult()
    object AlreadyExists: RegResult()
}

@Service
private class RegistrationService(
    private val repo: UserCredentialsRepository
) {

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun registerUser(email: String, password: String): RegResult =
        repo.findById(email).orElse(null) ?.let {
            RegResult.AlreadyExists
        } ?: let {
            repo.save(UserCredentialsEntity(email, password))
            RegResult.Success
        }

    fun getAllUsersCredentials(): List<UserCredentials> =
        repo.findAll().map { UserCredentials(it.email, it.password) }

}

private interface UserCredentialsRepository: JpaRepository<UserCredentialsEntity, String>

@Entity
@Table(name = "users")
private data class UserCredentialsEntity (

    /*
     * Почему я не использовал суррогатный ключ?
     * ответ тут: https://kotlinexpertise.com/hibernate-with-kotlin-spring-boot/
     */
    @Id
    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String
)