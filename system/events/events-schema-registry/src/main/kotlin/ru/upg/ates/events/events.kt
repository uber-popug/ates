package ru.upg.ates.events

import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID


interface Event<T> {
    val id: UUID
    val timestamp: Instant
    val name: String
    val version: Int
    val jsonSchemaId: String
    val producer: String
    val payload: T
}

/**
 * Marker interface to note Business Events
 */
interface BusinessEvent<T> : Event<T>

/**
 * Marker interface to note CUD (Create, Update, Delete) events
 */
interface CUDEvent<T> : Event<T>
