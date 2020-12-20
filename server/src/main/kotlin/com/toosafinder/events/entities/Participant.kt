package com.toosafinder.events.entities

import com.toosafinder.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "users")
class Participant(
    @Column(name = "login")
    val login: String,
) : BaseEntity<Long>()

interface ParticipantRepository : JpaRepository<Participant, Long> {
    fun findByLogin(login: String): Participant?
}