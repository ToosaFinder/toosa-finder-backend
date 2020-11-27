package com.toosafinder.events

import com.toosafinder.api.events.EventCreationErrors
import com.toosafinder.api.events.EventCreationReq
import com.toosafinder.api.events.EventCreationRes
import com.toosafinder.events.entities.*
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.security.entities.User
import com.toosafinder.security.entities.UserRepository
import com.toosafinder.webcommon.HTTP
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/event")
private class EventController(
    private val eventService: EventService
) {
    private val log by LoggerProperty()

    @PostMapping()
    fun createEvent(@RequestBody event: EventCreationReq): ResponseEntity<*> {
        log.trace("Event creation")

        return when (val eventCreationResult = eventService.createEvent(event)) {
            is EventCreationResult.Success -> HTTP.ok(
                EventCreationRes(
                    id = eventCreationResult.id,
                    name = eventCreationResult.event.name,
                    creator = eventCreationResult.event.creator.login,
                    description = eventCreationResult.event.description,
                    address = eventCreationResult.event.address,
                    latitude = eventCreationResult.event.latitude,
                    longitude = eventCreationResult.event.longitude,
                    participantsLimit = eventCreationResult.event.participantsLimit,
                    startTime = eventCreationResult.event.startTime,
                    isPublic = eventCreationResult.event.public,
                    tags = eventCreationResult.event.tags.map(Tag::name)
                )
            )
            is EventCreationResult.UserNotFound -> HTTP.conflict(
                code = EventCreationErrors.USER_NOT_FOUND.name
            )
        }
    }
}

@Service
private class EventService(
    private val eventRepository: EventRepository,
    private val tagRepository: TagRepository,
    private val participantRepository: ParticipantRepository
) {
    fun createEvent(event: EventCreationReq): EventCreationResult {
        val creator = participantRepository.findByLogin(event.creator) ?: return EventCreationResult.UserNotFound
        val newEvent = eventRepository.save(event.toEvent(creator))

        for (tagName in event.tags) {
            val tag = tagRepository.findByName(tagName)

            if (tag == null) {
                newEvent.tags.add(tagRepository.save(Tag(tagName)))
            } else {
                newEvent.tags.add(tag)
            }
        }

        return EventCreationResult.Success(newEvent.id!!, newEvent)
    }
}

private fun EventCreationReq.toEvent(creator: Participant) = Event (
        name,
        creator,
        description,
        address,
        latitude,
        longitude,
        participantsLimit,
        startTime,
        isPublic,
        false,
)

sealed class EventCreationResult {
    data class Success(val id: Long, val event: Event) : EventCreationResult()
    object UserNotFound : EventCreationResult()
}