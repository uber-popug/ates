package ru.upg.ates.events

import ru.upg.ates.Event
import java.time.Instant
import java.util.*

data class EmailCreated(
    override val producer: String,
    val email: String,
    val theme: String,
    val text: String,
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: Instant = Instant.now()
) : Event {
    override val jsonSchemaId = "#/emails/created/1"
    override val name = "EmailCreated"
    override val version = 1
}
