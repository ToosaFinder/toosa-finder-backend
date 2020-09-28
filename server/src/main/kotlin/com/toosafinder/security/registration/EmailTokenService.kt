package com.toosafinder.security.registration

import com.toosafinder.security.registration.entities.EmailToken
import com.toosafinder.security.registration.entities.EmailTokenRepository
import com.toosafinder.security.registration.entities.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
internal class EmailTokenService(
        @Value("\${email-tokens.expiration-time}")
        private val tokenExpirationTime: Long,

        private val emailTokenRepository: EmailTokenRepository
) {

    fun generateToken(user: User): EmailToken = emailTokenRepository.save(
            EmailToken(UUID.randomUUID(), user, LocalDateTime.now(), tokenExpirationTime)
    )

    fun validateToken(token: String): User? {
        val tokenValue = try {
            UUID.fromString(token)
        } catch (e: IllegalArgumentException) { null } ?: return null

        val emailToken = emailTokenRepository.findByValue(tokenValue) ?: return null
        val tokenLifetime = Duration.between(emailToken.creationTime, LocalDateTime.now()).seconds
        return if (tokenLifetime < emailToken.expirationTime) emailToken.user else null
    }

}