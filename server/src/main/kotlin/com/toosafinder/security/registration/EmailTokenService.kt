package com.toosafinder.security.registration

import com.toosafinder.security.entities.EmailToken
import com.toosafinder.security.entities.EmailTokenRepository
import com.toosafinder.security.entities.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
internal class EmailTokenService(
        @Value("\${email-tokens.expiration-time}")
        private val tokenExpirationTime: Duration,

        private val emailTokenRepository: EmailTokenRepository
) {

    fun generateToken(user: User): EmailToken = emailTokenRepository.save(
            EmailToken(UUID.randomUUID(), user, LocalDateTime.now())
    )

    fun validateToken(token: String): EmailTokenValidationResult {
        val tokenValue = uuidFromString(token)
            ?: return EmailTokenValidationResult.InvalidToken

        val emailToken = emailTokenRepository.findByValue(tokenValue)
            ?: return EmailTokenValidationResult.TokenNotFound
        val tokenLifetime = Duration.between(emailToken.creationTime, LocalDateTime.now())

        return if (tokenLifetime < tokenExpirationTime){
            EmailTokenValidationResult.Success(emailToken.user)
        } else{
            EmailTokenValidationResult.TokenExpired
        }
    }

}

private fun uuidFromString(src: String): UUID? {
    return try {
        UUID.fromString(src)
    } catch (e: IllegalArgumentException) {
        null
    }
}

internal sealed class EmailTokenValidationResult {
    class Success(val user: User): EmailTokenValidationResult()
    object InvalidToken: EmailTokenValidationResult()
    object TokenNotFound: EmailTokenValidationResult()
    object TokenExpired: EmailTokenValidationResult()
}