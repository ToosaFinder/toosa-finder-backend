package com.toosafinder.security.login

import com.toosafinder.email.EmailSendingResult
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.security.email.SecurityEmailService
import com.toosafinder.security.entities.UserRepository
import com.toosafinder.security.jwt.JwtTokenService
import com.toosafinder.security.jwt.JwtPayload
import com.toosafinder.security.registration.EmailTokenService
import com.toosafinder.security.registration.EmailTokenValidationResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class PasswordRestoreService(
    private val tokenService: EmailTokenService,
    private val emailService: SecurityEmailService,
    private val userRepository: UserRepository,
    private val jwtTokenService: JwtTokenService
) {

    private val log by LoggerProperty()

    fun restorePassword(email: String): PasswordRestoreResult {
        val user = userRepository.findByEmail(email)
            ?: return PasswordRestoreResult.UserNotFound
        val uuid = tokenService.generateToken(user).value

        return when(emailService.sendPasswordRestore(email, uuid)) {
            is EmailSendingResult.Success -> {
                log.debug("Successfully sent password restore letter to ${user.login}")
                PasswordRestoreResult.Success
            }
            is EmailSendingResult.SendingError -> PasswordRestoreResult.EmailSendingError
        }
    }

    @Transactional
    fun setPassword(emailToken: String, newPassword: String): PasswordSetResult {
        return when(val tokenValidationResult = tokenService.validateToken(emailToken)) {
            is EmailTokenValidationResult.Success -> {
                tokenValidationResult.user.password = newPassword

                log.debug("${tokenValidationResult.user.login} has successfully " +
                        "changed their password")
                PasswordSetResult.Success
            }
            else -> PasswordSetResult.TokenNotValid
        }
    }

    //TODO: проверить что пришло - логин или пароль
    //TODO: рефрещ
    fun login(loginOrEmail: String, password: String): LoginResult {
        val user = userRepository.findByEmail(loginOrEmail)
            ?: userRepository.findByLogin(loginOrEmail)
            ?: return LoginResult.Failure
        val accessToken = jwtTokenService.generateToken(JwtPayload(
            email = user.email,
            refreshTokenId = 1
        ))
        return LoginResult.Success(
            accessToken = accessToken,
            refreshToken = ""
        )
    }
}

internal sealed class PasswordRestoreResult {
    object Success: PasswordRestoreResult()
    object UserNotFound: PasswordRestoreResult()
    object EmailSendingError: PasswordRestoreResult()
}

internal sealed class PasswordSetResult {
    object Success: PasswordSetResult()
    object TokenNotValid: PasswordSetResult()
}