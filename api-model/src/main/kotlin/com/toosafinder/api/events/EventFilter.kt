package com.toosafinder.api.events

data class EventFilter (
    val tags: Set<String>,
    val name: String
)