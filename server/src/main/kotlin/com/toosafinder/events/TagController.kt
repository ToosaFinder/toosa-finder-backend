package com.toosafinder.events

import com.toosafinder.api.events.PopularTagRes
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
@RequestMapping("/event/tag")
private class TagController(
    private val tagService: TagService
) {

    private val log by LoggerProperty()

    @GetMapping("/popular")
    fun getPopularTags(): ResponseEntity<PopularTagRes> {
        log.trace("Fetching popular tags");
        val tags = tagService.getPopularTags().map(Tag::name)
        return HTTP.ok(PopularTagRes(tags))
    }
}

@Service
private class TagService(
    private val tagRepository: TagRepository
) {

    fun getPopularTags(): List<Tag> =
        tagRepository.findTopByPopularityByOrderDesc(100)
}
