package com.toosafinder.events

import com.toosafinder.api.events.GetTagsRes
import com.toosafinder.events.entities.EventRepository
import com.toosafinder.events.entities.Tag
import com.toosafinder.events.entities.TagRepository
import com.toosafinder.logging.LoggerProperty
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

    @GetMapping("/tags")
    fun getAllTags(): ResponseEntity<GetTagsRes> {
        log.trace("Fetching all event tags");
        val tags = eventService.getAllTags().map(EventMapper::toTagDto)
        return HTTP.ok(GetTagsRes(tags))
    }
}

@Service
private class EventService(
    private val eventRepository: EventRepository,
    private val tagRepository: TagRepository
) {

    fun getAllTags(): List<Tag> = tagRepository.findAllByOrderByPopularityDesc()
}

private class EventMapper {

    companion object {

        fun toTagDto(tag: Tag) = tag.name
    }
}