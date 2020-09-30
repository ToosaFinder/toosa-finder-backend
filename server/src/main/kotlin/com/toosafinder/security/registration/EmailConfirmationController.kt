package com.toosafinder.security.registration

import com.toosafinder.security.registration.entities.Role
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user/emailConfirmed")
private class EmailConfirmationController(
        private val emailTokenService: EmailTokenService,
        private val roleManagementService: RoleManagementService
) {

    @PutMapping("{token}")
    fun confirmEmail(@PathVariable token: String): ResponseEntity<Unit> {
        val validationResult = emailTokenService.validateToken(token)
        if (validationResult !is EmailTokenValidationResult.Success) {
            return ResponseEntity.badRequest().build()
        }

        val userId = validationResult.user.id!!
        val roleName = Role.Name.USER.toString()
        return when (roleManagementService.addRoleToUser(userId, roleName)) {
            is RoleManagementResult.Success -> ResponseEntity.ok().build()
            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

}
