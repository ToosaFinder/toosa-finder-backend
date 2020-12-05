package com.toosafinder.api.events

import java.time.LocalDateTime

data class GetEventRes(
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

        val isClosed: Boolean,

        val participants: List<String>,

        val tags: List<String>
)

enum class GetEventErrors {
    EVENT_NOT_FOUND,
}