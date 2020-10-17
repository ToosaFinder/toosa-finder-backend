package com.toosafinder.security.login

import com.toosafinder.logging.LoggerProperty
import com.toosafinder.security.entities.UserRepository
import com.toosafinder.security.jwt.JwtTokenService
import com.toosafinder.security.jwt.JwtPayload
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
internal class LoginService(
    private val userRepository: UserRepository,
    private val jwtTokenService: JwtTokenService,
    private val passwordEncoder: PasswordEncoder
) {

    private val log by LoggerProperty()

    //TODO: проверить что пришло - логин или пароль
    fun login(loginOrEmail: String, password: String): LoginResult {
        val user = userRepository.findByEmail(loginOrEmail)
            ?: userRepository.findByLogin(loginOrEmail)

        if(user == null) {
            log.debug("user {} not found", loginOrEmail)
            return LoginResult.Failure
        }

        if(!passwordEncoder.matches(password, user.password)){
            log.debug("password of user {} does not match", loginOrEmail)
            return LoginResult.Failure
        }

        val accessToken = jwtTokenService.generateToken(
            JwtPayload(
                email = user.email,
                refreshTokenId = 1
            ))
        return LoginResult.Success(
            accessToken = accessToken
        )
    }
}

internal sealed class LoginResult {
    object Failure: LoginResult()
    data class Success(
        val accessToken: String,
        val refreshToken: String? = null
    ): LoginResult()
}
