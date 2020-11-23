package com.toosafinder.events

import com.toosafinder.api.events.EventCreationErrors
import com.toosafinder.api.events.EventCreationReq
import com.toosafinder.api.events.EventCreationRes
import com.toosafinder.events.entities.Event
import com.toosafinder.events.entities.EventRepository
import com.toosafinder.events.entities.Tag
import com.toosafinder.events.entities.TagRepository
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
                    eventId = eventCreationResult.eventId,
                    eventCreationReq = event
                )
            )
            is EventCreationResult.UserNotFound -> HTTP.conflict(
                code = EventCreationErrors.USER_BY_LOGIN_NOT_FOUND.name
            )
        }
    }
}

@Service
private class EventService(
    private val eventRepository: EventRepository,
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository
) {
    fun createEvent(event: EventCreationReq): EventCreationResult {
        val creator = userRepository.findByLogin(event.creator) ?: return EventCreationResult.UserNotFound
        val newEvent = eventRepository.save(EventMapper.toEvent(event, creator))

        for (tagName in event.tags) {
            val tag = tagRepository.findByName(tagName)

            if (tag == null) {
                newEvent.tags.add(tagRepository.save(Tag(tagName)))
            } else {
                newEvent.tags.add(tag)
            }
        }

        return EventCreationResult.Success(newEvent.id)
    }
}

private class EventMapper {
    companion object {
        fun toEvent(event: EventCreationReq, creator: User) = Event(
            event.name,
            creator,
            event.description,
            event.address,
            event.latitude,
            event.longitude,
            event.participantsLimit,
            event.startTime,
            event.isPublic,
            false,
        )
    }
}

sealed class EventCreationResult {
    data class Success(val eventId: Long?) : EventCreationResult()
    object UserNotFound : EventCreationResult()
}