package com.toosafinder.api.auth;

import com.toosafinder.api.LocalStorage

internal const val ACCESS_TOKEN_KEY = "TF_ACCESS_TOKEN"
internal const val REFRESH_TOKEN_KEY = "TF_REFRESH_TOKEN"

internal data class Tokens(
        val accessToken: String
)

internal class TokenStorage(
    private val storage: LocalStorage,
    private val onLogIn: () -> Unit,
    private val onLogOut: () -> Unit
){

    internal val accessToken
        get() = storage.get(ACCESS_TOKEN_KEY)

    fun isLoggedIn(): Boolean =
        storage.get(ACCESS_TOKEN_KEY) != null

    internal fun login(tokens: Tokens) {
        storage.set(ACCESS_TOKEN_KEY, tokens.accessToken);
        onLogIn()
    }

    internal fun logout() {
        storage.remove(ACCESS_TOKEN_KEY)
        onLogOut()
    }

}
