package ru.upg.ates.events

import ru.upg.ates.Event

@Event("#/emails/created/1.yaml", "EmailCreated", 1)
data class EmailCreated(
    val email: String,
    val theme: String,
    val text: String
)
