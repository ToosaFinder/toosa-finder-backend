package com.toosafinder.security

import com.toosafinder.security.entities.User
import com.toosafinder.security.entities.UserRepository
import com.toosafinder.security.jwt.JwtTokenService
import com.toosafinder.security.jwt.JwtTokenValidationResult
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class TokenAuthenticationManager(
    private val userRepo: UserRepository,
    private val tokenService: JwtTokenService
) : AuthenticationManager {

//    private val log by LoggerProperty()

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        authentication as? TokenAuthentication
            ?: throw BadCredentialsException("authentication must be TokenAuthentication")

        return when (val res = tokenService.validateToken(authentication.token)){
            is JwtTokenValidationResult.TokenExpired -> throw BadCredentialsException("token expired")
            is JwtTokenValidationResult.SignatureInvalid -> throw BadCredentialsException("bad signature")
            is JwtTokenValidationResult.Success -> {
                userRepo.findByEmail(res.jwtPayload.email)
                    ?.let { UserAuthentication(AuthenticatedUserDetails(it)) }
                    ?: throw BadCredentialsException("user not found by token")
            }
        }
    }
}

class UserAuthentication(
    private val principal: AuthenticatedUserDetails
) : Authentication {

    override fun getPrincipal() = principal
    override fun getAuthorities(): Collection<GrantedAuthority> =
        principal.user.roles.map { SimpleGrantedAuthority(it.name) }

    override fun setAuthenticated(isAuthenticated: Boolean) {}
    override fun getName(): String = principal.username
    override fun getCredentials(): Any? = principal.password
    override fun isAuthenticated(): Boolean = true
    override fun getDetails(): Any? = null
}

class AuthenticatedUserDetails(
    val user: User
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        user.roles.map { SimpleGrantedAuthority(it.name) }
    override fun isEnabled(): Boolean = true
    override fun getUsername(): String = user.email
    override fun isCredentialsNonExpired(): Boolean = true
    override fun getPassword(): String? = null
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
}
