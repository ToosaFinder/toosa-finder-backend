package com.toosafinder.security.entities

import com.toosafinder.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "email_tokens")
internal class EmailToken(

        @Column(name = "value")
        val value: UUID,

        @ManyToOne
        @JoinColumn(name = "user_id")
        val user: User,

        @Column(name = "creation_time")
        val creationTime: LocalDateTime,

): BaseEntity<Long>()

internal interface EmailTokenRepository: JpaRepository<EmailToken, Long> {
        fun findByValue(value: UUID): EmailToken?
}