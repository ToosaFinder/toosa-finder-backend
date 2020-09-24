package com.toosafinder.security.registration.entities

import com.toosafinder.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
internal class User(

        @Column(name = "email")
        val email: String,

        @Column(name = "login")
        val login: String,

        @Column(name = "password")
        val password: String,

        @Column(name = "registration_ts")
        val registrationTime: LocalDateTime

) : BaseEntity<Long>() {

        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JoinTable(
                name = "users_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")]
        )
        val roles: MutableSet<Role> = hashSetOf()

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
        val registrationConfirmations: MutableSet<RegistrationConfirmation> = hashSetOf()

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
        val passwordChangeConfirmations: MutableSet<PasswordChangeConfirmation> = hashSetOf()

}

internal interface UserRepository: JpaRepository<User, Long> {
        fun findByEmail(email: String): User?
}