package com.toosafinder.events

import com.toosafinder.api.events.*
import com.toosafinder.events.entities.*
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.security.AuthorizedUserInfo
import com.toosafinder.security.entities.User
import com.toosafinder.webcommon.HTTP
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/event")
private class EventController(
    private val eventService: EventService
) {
    private val log by LoggerProperty()

    @GetMapping("/{id}")
    fun getEvent(@PathVariable id: Long): ResponseEntity<*> {
        log.trace("Fetching event with id $id")

        return when (val eventFetchingResult = eventService.getEvent(id)) {
            is EventFetchingResult.Success -> HTTP.ok(eventFetchingResult.event.toDto())
            is EventFetchingResult.EventNotFound -> HTTP.conflict(
                code = GetEventErrors.EVENT_NOT_FOUND.name
            )
        }
    }

    @GetMapping
    fun getActiveEvents(): ResponseEntity<GetEventsRes> {
        log.debug("Fetching all active events")
        val events = eventService.getAllActiveEvents().map(Event::toDto)
        return HTTP.ok(GetEventsRes(events))
    }

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
                    isPublic = eventCreationResult.event.isPublic,
                    tags = eventCreationResult.event.tags.map(Tag::name)
                )
            )
            is EventCreationResult.UserNotFound -> HTTP.conflict(
                code = EventCreationErrors.USER_NOT_FOUND.name
            )
        }
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
    private val eventRepository: EventRepository,
    private val tagRepository: TagRepository,
    private val participantRepository: ParticipantRepository
) {

    fun getEvent(id: Long): EventFetchingResult {
        val event = eventRepository.findByIdOrNull(id)

        return if (event != null) {
            EventFetchingResult.Success(event)
        } else {
            EventFetchingResult.EventNotFound
        }
    }

    fun getAllActiveEvents(): List<Event> = eventRepository.getAllByIsClosedIsFalse()

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

private fun Event.toDto() = GetEventRes (
    id = id!!,
    name = name,
    creator = creator.login,
    description = description,
    address = address,
    latitude = latitude,
    longitude = longitude,
    participantsLimit = participantsLimit,
    startTime = startTime,
    isPublic = isPublic,
    isClosed = isClosed,
    participants = participants.map(User::login),
    tags = tags.map(Tag::name)
)

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

sealed class EventFetchingResult {

    data class Success(val event: Event): EventFetchingResult()

    object EventNotFound: EventFetchingResult()
}

sealed class EventCreationResult {

    data class Success(val id: Long, val event: Event) : EventCreationResult()

    object UserNotFound : EventCreationResult()
}

sealed class EventDeletionResult {

    object Success: EventDeletionResult()

    object EventNotFound: EventDeletionResult()

    object BadPermissions: EventDeletionResult()

}