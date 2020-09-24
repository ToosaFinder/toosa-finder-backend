package com.toosafinder.security.registration.entities

import com.toosafinder.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "registration_confirmations")
internal class RegistrationConfirmation(

        @Column(name = "uuid")
        val uuid: UUID,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        val user: User,

        @Column(name = "creation_time")
        val creationTime: LocalDateTime,

        @Column(name = "confirmation_time")
        val confirmationTime: LocalDateTime,

        @Column(name = "is_active")
        val isActive: Boolean = true

): BaseEntity<Long>()

internal interface EmailConfirmationRepository: JpaRepository<RegistrationConfirmation, Long>