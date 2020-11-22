package com.toosafinder.events

import com.toosafinder.api.events.PopularTagRes
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

    @GetMapping("/tag/popular")
    fun getPopularTags(): ResponseEntity<PopularTagRes> {
        log.trace("Fetching popular tags");
        val tags = eventService.getPopularTags().map(EventMapper::toTagDto)
        return HTTP.ok(PopularTagRes(tags))
    }
}

@Service
private class EventService(
    private val eventRepository: EventRepository,
    private val tagRepository: TagRepository
) {

    fun getPopularTags(): List<Tag> =
        tagRepository.findTopByPopularityByOrderDesc(100)
}

private class EventMapper {

    companion object {

        fun toTagDto(tag: Tag) = tag.name
    }
}