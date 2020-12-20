package com.toosafinder.security.jwt

import org.junit.jupiter.api.*

const val SECRET = "dG9vc2FGaW5kZXJWZXJ5VmVyeVN0cm9uZ1NlY3JldFRlc3RLZXk="
const val SHORT_VALIDITY = 1L
const val VALIDITY = 3600000L


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class JwtTokenServiceTest() {
    private val jwtPayload: JwtPayload = JwtPayload("ivan_ivanov1337@gmail.com", 1)

    @Test
    @Order(1)
    fun `should return success validation result of generated token`() {
        val jwtTokenService = JwtTokenService(SECRET, VALIDITY)

        val token = jwtTokenService.generateToken(jwtPayload)

        when (val validationResult = jwtTokenService.validateToken(token)) {
            is JwtTokenValidationResult.Success -> {
                Assertions.assertEquals(this.jwtPayload.email, validationResult.jwtPayload.email)
                Assertions.assertEquals(this.jwtPayload.refreshTokenId, validationResult.jwtPayload.refreshTokenId)
            }
        }
    }

    @Test
    @Order(2)
    @Disabled
    fun `should return error validation result by expiration`() {
        val jwtTokenService = JwtTokenService(SECRET, SHORT_VALIDITY)

        val token = jwtTokenService.generateToken(jwtPayload)

        //TODO: ну нах
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