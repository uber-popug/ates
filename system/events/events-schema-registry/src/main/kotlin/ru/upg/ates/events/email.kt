package ru.upg.ates.events

import ru.upg.ates.Event

data class EmailCreated(
    override val producer: String,
    val email: String,
    val theme: String,
    val text: String,
) : Event {
    override val jsonSchemaId = "#/emails/created/1"
    override val name = "EmailCreated"
    override val version = 1
}
