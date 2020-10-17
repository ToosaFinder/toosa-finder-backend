package com.toosafinder.security.entities

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
        var password: String,

        @Column(name = "registration_time")
        val registrationTime: LocalDateTime

) : BaseEntity<Long>() {

        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JoinTable(
                name = "users_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")]
        )
        val roles: MutableSet<Role> = hashSetOf()

}

internal interface UserRepository: JpaRepository<User, Long> {

        fun existsByLogin(login: String): Boolean

        fun existsByEmail(email: String): Boolean

        fun findByEmail(email: String): User?

}