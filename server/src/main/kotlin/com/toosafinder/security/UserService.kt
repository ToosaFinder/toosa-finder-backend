package com.toosafinder.security

import com.toosafinder.security.entities.Role
import com.toosafinder.security.entities.RoleRepository
import com.toosafinder.security.entities.User
import com.toosafinder.security.entities.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
internal class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun createUser(login: String, email: String, password: String): UserCreationResult {
        if (userRepository.existsByLogin(login)) {
            return UserCreationResult.LoginDuplication
        }

        if (userRepository.existsByEmail(email)) {
            return UserCreationResult.EmailDuplication
        }

        val user = User(
            email = email,
            login = login,
            password = passwordEncoder.encode(password),
            registrationTime = LocalDateTime.now()
        ).let {
            val roleName = Role.Name.UNCONFIRMED.name
            it.roles.add(roleRepository.findByName(roleName)!!)
            userRepository.save(it)
        }

        return UserCreationResult.Success(user)
    }

}

internal sealed class UserCreationResult {

    class Success(val user: User) : UserCreationResult()

    object LoginDuplication: UserCreationResult()

    object EmailDuplication: UserCreationResult()

}
