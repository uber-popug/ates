package ru.upg.ates

import java.time.Instant
import java.util.UUID

interface Event {
    val id: UUID
    val timestamp: Instant
    val jsonSchemaId: String
    val name: String
    val version: Int
    val producer: String
}
