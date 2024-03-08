package ru.upg.ates.events

import java.time.Instant
import java.util.*

abstract class Event<T>(
    open val id: UUID = UUID.randomUUID(),
    open val timestamp: Instant = Instant.now(),
) {
    abstract val payload: T
    abstract val jsonSchemaId: String
    abstract val name: String
    abstract val version: Int
    abstract val producer: String
}
