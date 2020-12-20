package com.toosafinder.security

import org.springframework.security.core.context.SecurityContextHolder

internal class AuthorizedUserInfo {

    companion object {

        fun getUserId(): Long =
                getPrincipal().user.id!!

        fun getUserLogin(): String =
                getPrincipal().user.login

        fun getUserEmail(): String =
                getPrincipal().user.email

        private fun getPrincipal(): AuthenticatedUserDetails =
                SecurityContextHolder
                        .getContext()
                        .authentication
                        .principal as AuthenticatedUserDetails

    }

}