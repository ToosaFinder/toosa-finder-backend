package com.toosafinder.security.registration

import com.toosafinder.security.entities.Role
import com.toosafinder.webcommon.HTTP
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

enum class EmailConfirmErrCode{
    INVALID_EMAIL_TOKEN
}

@RestController
@RequestMapping("/user/email-confirmed")
private class EmailConfirmationController(
        private val emailTokenService: EmailTokenService,
        private val roleManagementService: RoleManagementService
) {

    @PutMapping("{token}")
    fun confirmEmail(@PathVariable token: String): ResponseEntity<*> {
        val validationResult = emailTokenService.validateToken(token)
        if (validationResult !is EmailTokenValidationResult.Success) {
            return HTTP.conflict(code = EmailConfirmErrCode.INVALID_EMAIL_TOKEN.name)
        }

        val userId = validationResult.user.id!!
        val roleName = Role.Name.USER.toString()
        roleManagementService.addRoleToUser(userId, roleName)
        return HTTP.ok()
    }

}
