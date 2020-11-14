package com.toosafinder.events.entities

import com.toosafinder.BaseEntity
import com.toosafinder.security.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "events")
class Event(

        @Column(name = "name")
        val name: String,

        @ManyToOne
        @JoinColumn(name = "creator_id")
        val creator: User,

        @Column(name = "description")
        val description: String,

        @Column(name = "address")
        val address: String,

        @Column(name = "latitude")
        val latitude: Float,

        @Column(name = "longitude")
        val longitude: Float,

        @Column(name = "participants_limit")
        val participantsLimit: Int,

        @Column(name = "start_time")
        val startTime: LocalDateTime,

        @Column(name = "is_public")
        val isPublic: Boolean,

        @Column(name = "is_closed")
        val isClosed: Boolean

) : BaseEntity<Long>() {

        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JoinTable(
            name = "events_participants",
            joinColumns = [JoinColumn(name = "event_id")],
            inverseJoinColumns = [JoinColumn(name = "participant_id")]
        )
        val participants: MutableSet<User> = hashSetOf()

        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JoinTable(
                name = "events_administrators",
                joinColumns = [JoinColumn(name = "event_id")],
                inverseJoinColumns = [JoinColumn(name = "administrator_id")]
        )
        val administrators: MutableSet<User> = hashSetOf()

}

interface EventRepository: JpaRepository<Event, Long>