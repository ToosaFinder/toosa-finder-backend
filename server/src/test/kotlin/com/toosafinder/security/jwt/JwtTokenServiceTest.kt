package com.toosafinder.security.jwt

import org.junit.jupiter.api.*

const val SECRET = "dG9vc2FGaW5kZXJWZXJ5VmVyeVN0cm9uZ1NlY3JldFRlc3RLZXk="
const val SHORT_VALIDITY = 1L
const val VALIDITY = 3600000L


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class JwtTokenServiceTest() {
    private val payload: Payload = Payload("ivan_ivanov1337@gmail.com", 1)

    @Test
    @Order(1)
    fun `should return success validation result of generated token`() {
        val jwtTokenService = JwtTokenService(SECRET, VALIDITY)

        val token = jwtTokenService.generateToken(payload)

        when (val validationResult = jwtTokenService.validateToken(token)) {
            is JwtTokenValidationResult.Success -> {
                Assertions.assertEquals(this.payload.email, validationResult.payload.email)
                Assertions.assertEquals(this.payload.refreshTokenId, validationResult.payload.refreshTokenId)
            }
        }
    }

    @Test
    @Order(2)
    fun `should return error validation result by expiration`() {
        val jwtTokenService = JwtTokenService(SECRET, SHORT_VALIDITY)

        val token = jwtTokenService.generateToken(payload)

        Thread.sleep(10)

        assert(jwtTokenService.validateToken(token) is JwtTokenValidationResult.TokenExpired)
    }

    @Test
    @Order(4)
    fun `should return success validation result by uncorrect token`() {
        val jwtTokenService = JwtTokenService(SECRET, VALIDITY)
        val wrongToken = "wrongToken"

        assert(jwtTokenService.validateToken(wrongToken) is JwtTokenValidationResult.SignatureInvalid)
    }
}