package com.toosafinder.events

import com.toosafinder.api.events.EventDto
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
        val events = eventService.getActiveEvents().map(EventMapper::toDto)
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

private class EventMapper {

    companion object {
        fun toDto(e: Event) = EventDto (
            name = e.name,
            creator = e.creator.login,
            description = e.description,
            address = e.address,
            latitude = e.latitude,
            longitude = e.longitude,
            participantsLimit = e.participantsLimit,
            startTime = e.startTime,
            isPublic = e.public,
            isClosed = e.closed,
            participants = e.participants.map(User::login),
            tags = e.tags.map(Tag::name)
        )
    }

}