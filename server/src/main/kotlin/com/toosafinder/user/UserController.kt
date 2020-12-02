package com.toosafinder.user

import com.toosafinder.api.user.UserRes
import com.toosafinder.security.AuthorizedUserInfo
import com.toosafinder.webcommon.HTTP
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
private class UserController() {

    @GetMapping("/me")
    fun getUserInfo(): ResponseEntity<UserRes> =
        HTTP.ok(
            UserRes(
                login = AuthorizedUserInfo.getUserLogin(),
                email = AuthorizedUserInfo.getUserEmail()
            )
        )

}