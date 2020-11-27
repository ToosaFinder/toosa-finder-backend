package com.toosafinder.events

import com.toosafinder.api.events.EventCreationErrors
import com.toosafinder.api.events.EventCreationReq
import com.toosafinder.api.events.EventCreationRes
import com.toosafinder.api.events.EventDeletionErrors
import com.toosafinder.events.entities.*
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.security.AuthorizedUserInfo
import com.toosafinder.webcommon.HTTP
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

sealed class EventDeletionResult {

    object Success: EventDeletionResult()

    object EventNotFound: EventDeletionResult()

    object BadPermissions: EventDeletionResult()

}