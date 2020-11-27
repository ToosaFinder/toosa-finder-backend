package com.toosafinder.security

import org.springframework.security.core.context.SecurityContextHolder

internal class AuthorizedUserInfo {

    companion object {

        fun getUserId(): Long =
            getPrincipal().user.id!!

        private fun getPrincipal(): AuthenticatedUserDetails =
                SecurityContextHolder
                        .getContext()
                        .authentication
                        .principal as AuthenticatedUserDetails

    }

}