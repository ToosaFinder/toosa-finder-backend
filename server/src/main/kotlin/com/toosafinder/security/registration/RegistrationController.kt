package com.toosafinder.security.registration

//import com.toosafinder.api.UserCredentials
//import com.toosafinder.security.registration.entities.User
//import com.toosafinder.security.registration.entities.UserRepository
//import com.toosafinder.webcommon.HTTP
//import io.konform.validation.Invalid
//import io.konform.validation.Validation
//import io.konform.validation.jsonschema.maxLength
//import io.konform.validation.jsonschema.minLength
//import org.springframework.http.ResponseEntity
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Isolation
//import org.springframework.transaction.annotation.Transactional
//import org.springframework.web.bind.annotation.*
//
//@RestController
//@RequestMapping("/user/registration")
//private class RegistrationController(
//    private val registrationService: RegistrationService
//) {
//
//    val validateUser = Validation<UserCredentials> {
//        UserCredentials::email {
//            minLength(2)
//            maxLength(320)
//        }
//
//        UserCredentials::password {
//            minLength(8)
//        }
//    }
//
//    @PutMapping
//    fun registerUser(@RequestBody userCredentials: UserCredentials): ResponseEntity<*> {
//        when(val validationResult = validateUser(userCredentials)){
//            is Invalid -> return HTTP.badRequest(validationResult)
//        }
//
//        return when(registrationService.registerUser(userCredentials.email, userCredentials.password)){
//            is RegResult.Success -> HTTP.ok<Unit>()
//            is RegResult.AlreadyExists -> HTTP.conflict<Unit>()
//        }
//    }
//
//    @GetMapping
//    fun getUsersCredentials(): List<UserCredentials> =
//        registrationService.getAllUsersCredentials()
//
//}
//
//private sealed class RegResult {
//    object Success: RegResult()
//    object AlreadyExists: RegResult()
//}
//
//@Service
//private class RegistrationService(
//    private val repo: UserRepository
//) {
//
//    @Transactional(isolation = Isolation.REPEATABLE_READ)
//    fun registerUser(email: String, password: String): RegResult =
//        repo.findByEmail(email)?.let {
//            RegResult.AlreadyExists
//        } ?: let {
//            repo.save(User(email, password, ))
//            RegResult.Success
//        }
//
//    fun getAllUsersCredentials(): List<UserCredentials> =
//        repo.findAll().map { UserCredentials(it.email, it.password) }
//
//}