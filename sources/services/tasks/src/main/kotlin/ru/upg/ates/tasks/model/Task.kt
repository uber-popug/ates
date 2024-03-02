package ru.upg.ates.tasks.model

import java.util.UUID

data class Task(
    val id: UUID,
    val name: String,
    val userId: UUID
)