package com.toosafinder.events

import com.toosafinder.api.events.EventRes
import com.toosafinder.api.events.GetEventsRes
import com.toosafinder.events.entities.Event
import com.toosafinder.events.entities.EventRepository
import com.toosafinder.events.entities.Tag
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.security.entities.User
import com.toosafinder.webcommon.HTTP
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
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

}

@Service
private class EventService(
    private val eventRepository: EventRepository
) {

    fun getActiveEvents(): List<Event> =
        eventRepository.getAllByClosedIsFalse()

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
