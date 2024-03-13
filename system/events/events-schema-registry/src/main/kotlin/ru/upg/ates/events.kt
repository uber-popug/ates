package ru.upg.ates

import com.fasterxml.jackson.databind.node.ObjectNode
import java.time.Instant
import java.util.UUID

annotation class Event(
    val jsonSchemaId: String,
    val name: String,
    val version: Int
)

data class AtesEvent(
    val id: UUID = UUID.randomUUID(),
    val timestamp: Instant = Instant.now(),
    val jsonSchemaId: String,
    val name: String,
    val version: Int,
    val producer: String,
    val payload: ObjectNode
)
