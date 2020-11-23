package com.toosafinder.api.events

import java.time.LocalDateTime

data class EventCreationReq(
    val name: String,

    val creator: String,

    val description: String,

    val address: String,

    val latitude: Float,

    val longitude: Float,

    val participantsLimit: Int,

    val startTime: LocalDateTime,

    val isPublic: Boolean,

    val tags: List<String>
)

data class EventCreationRes(
    val eventId: Long?,

    val eventCreationReq: EventCreationReq
)

enum class EventCreationErrors {
    USER_BY_LOGIN_NOT_FOUND,
}