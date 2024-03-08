package ru.upg.ates

interface Event {
    val jsonSchemaId: String
    val name: String
    val version: Int
    val producer: String
}
