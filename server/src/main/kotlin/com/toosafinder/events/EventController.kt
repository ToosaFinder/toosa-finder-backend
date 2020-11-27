package com.toosafinder.events

import com.toosafinder.api.events.EventDeletionErrors
import com.toosafinder.api.events.EventRes
import com.toosafinder.api.events.GetEventsRes
import com.toosafinder.events.entities.Event
import com.toosafinder.events.entities.EventRepository
import com.toosafinder.events.entities.Tag
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.security.AuthorizedUserInfo
import com.toosafinder.security.entities.User
import com.toosafinder.webcommon.HTTP
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/event")
private class EventController(
        private val eventService: EventService
) {

    private val log by LoggerProperty()

    @GetMapping
    fun getActiveEvents(): ResponseEntity<GetEventsRes> {
        log.debug("Fetching all active events")
        val events = eventService.getActiveEvents().map(Event::toDto)
        return HTTP.ok(GetEventsRes(events))
    }

    @DeleteMapping("/{id}")
    fun deleteEvent(@PathVariable("id") eventId: Long): ResponseEntity<*> {
        val authorizedUserId = AuthorizedUserInfo.getUserId()
        log.debug("User #$authorizedUserId tries to delete event #$eventId")
        return when (eventService.deleteEvent(eventId, authorizedUserId)) {
            is EventDeletionResult.Success -> HTTP.ok()
            is EventDeletionResult.EventNotFound -> HTTP.conflict(
                code = EventDeletionErrors.EVENT_NOT_FOUND.name,
                message = "Event was not found"
            )
            is EventDeletionResult.BadPermissions -> HTTP.conflict(
                code = EventDeletionErrors.BAD_PERMISSIONS.name,
                message = "Authorized user is not owner of the specified event"
            )
        }
    }

}

@Service
private class EventService(
    private val eventRepository: EventRepository
) {

    fun getActiveEvents(): List<Event> =
        eventRepository.getAllByClosedIsFalse()

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun deleteEvent(eventId: Long, authorizedUserId: Long): EventDeletionResult {
        val event = eventRepository.findById(eventId)
                .orElse(null) ?: return EventDeletionResult.EventNotFound

        val creatorId = event.creator.id!!
        if (creatorId != authorizedUserId) {
            return EventDeletionResult.BadPermissions
        }

        eventRepository.delete(event)
        return EventDeletionResult.Success
    }

}

sealed class EventDeletionResult {

    object Success: EventDeletionResult()

    object EventNotFound: EventDeletionResult()

    object BadPermissions: EventDeletionResult()

}

private fun Event.toDto() = EventRes (
    id = id!!,
    name = name,
    creator = creator.login,
    description = description,
    address = address,
    latitude = latitude,
    longitude = longitude,
    participantsLimit = participantsLimit,
    startTime = startTime,
    isPublic = public,
    isClosed = closed,
    participants = participants.map(User::login),
    tags = tags.map(Tag::name)
)
