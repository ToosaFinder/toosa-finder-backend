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
    val id: Long,
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

enum class EventCreationErrors {
    USER_NOT_FOUND,
}