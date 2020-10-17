package com.toosafinder.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest

class TcsAuthHeaderFilter(
    authenticationManager: AuthenticationManager
) : AuthenticationFilter(authenticationManager, TcsAuthenticationConverter()) {

    init {
        //отключаем дефолтный хэндлер, чтобы не было редиректов
        successHandler = AuthenticationSuccessHandler { _, _, _ -> }
    }

    private class TcsAuthenticationConverter : AuthenticationConverter {

        override fun convert(request: HttpServletRequest): TokenAuthentication? =
            request.getHeader("X-Tcs-Token")
                ?.let { TokenAuthentication(it) }
    }
}

class TokenAuthentication(
    val token: String
) : Authentication {

    override fun getAuthorities(): Collection<GrantedAuthority>? = null
    override fun getCredentials(): Any? = null
    override fun getDetails(): Any? = null
    override fun getName(): String? = null
    override fun getPrincipal(): Any? = null
    override fun isAuthenticated(): Boolean = false
    override fun setAuthenticated(b: Boolean) {}
}

