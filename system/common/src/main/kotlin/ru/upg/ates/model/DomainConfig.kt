package ru.upg.ates.model

class DomainConfig(
    val kafkaUrl: String,
    val db: Db
) {
    data class Db(
        val url: String,
        val username: String,
        val password: String
    )
}