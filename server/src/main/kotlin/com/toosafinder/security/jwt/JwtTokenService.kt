package com.toosafinder.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

data class Payload(val email: String, val refreshTokenId: Int)

sealed class JwtTokenValidationResult {
    data class Success(val payload: Payload) : JwtTokenValidationResult()
    data class TokenExpired(val message: String) : JwtTokenValidationResult()
    data class SignatureInvalid(val message: String) : JwtTokenValidationResult()
}

sealed class ParseClaimsJwsResult {
    data class Success(val claims: Claims) : ParseClaimsJwsResult()
    data class TokenExpired(val message: String, val cause: Exception? = null) : ParseClaimsJwsResult()
    data class SignatureInvalid(val message: String, val cause: Exception? = null) : ParseClaimsJwsResult()
}

@Component
class JwtTokenService(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.expiration}")
    private val validityInMilliSeconds: Long

) {
    fun generateToken(payload: Payload): String {
        val claims: Map<String, Any> = HashMap()

        val now = Date()
        val validity = Date(now.time + validityInMilliSeconds)

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(payload.email)
            .setId(payload.refreshTokenId.toString())
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(getSigningKey())
            .compact()
    }

    fun validateToken(token: String): JwtTokenValidationResult {
        return when (val parseClaimsJwsResult = getAllClaims(token)) {
            is ParseClaimsJwsResult.Success -> JwtTokenValidationResult.Success(
                Payload(
                    parseClaimsJwsResult.claims.subject,
                    parseClaimsJwsResult.claims.id.toInt()
                )
            )
            is ParseClaimsJwsResult.SignatureInvalid -> JwtTokenValidationResult.SignatureInvalid(parseClaimsJwsResult.message)
            is ParseClaimsJwsResult.TokenExpired -> JwtTokenValidationResult.TokenExpired(parseClaimsJwsResult.message)
        }
    }

    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private fun getAllClaims(token: String): ParseClaimsJwsResult = try {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body
        ParseClaimsJwsResult.Success(claims)
    } catch (ex: ExpiredJwtException) {
        ParseClaimsJwsResult.TokenExpired(
            message = "Jwt token is expired",
            cause = ex
        )
    } catch (ex: JwtException) {
        ParseClaimsJwsResult.SignatureInvalid(
            message = "Jwt token is invalid",
            cause = ex
        )
    }
}