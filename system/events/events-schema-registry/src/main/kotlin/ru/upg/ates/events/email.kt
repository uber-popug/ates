package ru.upg.ates.events

data class EmailCreated(
    override val producer: String,
    override val payload: Payload,
) : Event<EmailCreated.Payload>() {

    data class Payload(
        val email: String,
        val theme: String,
        val text: String,
    )

    override val jsonSchemaId = "#/emails/created/1"
    override val name = "EmailCreated"
    override val version = 1
}
