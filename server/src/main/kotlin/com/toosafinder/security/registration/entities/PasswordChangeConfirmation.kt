package com.toosafinder.security.registration.entities

import com.toosafinder.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "password_change_confirmations")
internal class PasswordChangeConfirmation(

        @Column(name = "uuid")
        val uuid: UUID,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        val user: User,

): BaseEntity<Long>()

internal interface PasswordChangeConfirmationRepository: JpaRepository<PasswordChangeConfirmation, Long>

