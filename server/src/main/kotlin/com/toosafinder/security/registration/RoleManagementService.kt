package com.toosafinder.security.registration

import com.toosafinder.security.registration.entities.Role
import com.toosafinder.security.registration.entities.RoleRepository
import com.toosafinder.security.registration.entities.User
import com.toosafinder.security.registration.entities.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class RoleManagementService(
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository
) {

    fun addRoleToUser(userId: Long, roleName: String): RoleManagementResult =
            doActionWithUserRole(userId, roleName) { roles.add(it) }

    fun removeRoleFromUser(userId: Long, roleName: String): RoleManagementResult =
            doActionWithUserRole(userId, roleName) { roles.remove(it) }

    private inline fun doActionWithUserRole(
            userId: Long, roleName: String, action: (User).(Role) -> Unit
    ): RoleManagementResult {
        val role = roleRepository.findByName(roleName) ?: return RoleManagementResult.RoleNotFound
        val user = userRepository.findById(userId).orElse(null) ?: return RoleManagementResult.UserNotFound
        user.action(role)
        userRepository.save(user)
        return RoleManagementResult.Success
    }

}

internal sealed class RoleManagementResult {

    object Success: RoleManagementResult()

    object RoleNotFound: RoleManagementResult()

    object UserNotFound: RoleManagementResult()

}