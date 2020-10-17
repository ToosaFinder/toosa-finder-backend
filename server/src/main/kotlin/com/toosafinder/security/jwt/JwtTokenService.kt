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
    object TokenExpired: JwtTokenValidationResult()
    object SignatureInvalid: JwtTokenValidationResult()
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
            is ParseClaimsJwsResult.SignatureInvalid -> JwtTokenValidationResult.SignatureInvalid
            is ParseClaimsJwsResult.TokenExpired -> JwtTokenValidationResult.TokenExpired
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
        ParseClaimsJwsResult.TokenExpired
    } catch (ex: JwtException) {
        ParseClaimsJwsResult.SignatureInvalid
    }
}

private sealed class ParseClaimsJwsResult {
    data class Success(val claims: Claims) : ParseClaimsJwsResult()
    object TokenExpired: ParseClaimsJwsResult()
    object SignatureInvalid: ParseClaimsJwsResult()
}
